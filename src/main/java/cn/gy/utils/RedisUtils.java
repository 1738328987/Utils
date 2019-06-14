package cn.gy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * RedisAPI
 * @author bdqn_shang
 * @date 2018-1-10
 */
@Component
public class RedisUtils{

	private Logger logger = LoggerFactory.getLogger(RedisUtils.class);

	@Resource
	//读取配置文件中 redis 中的链接信息，并生成spring 组件对象
	private RedisTemplate<String,Object> redisTemplate;
	/***
	 * key 分布式事务锁
	 * @param _key
	 * @return
	 */
	public boolean nlock(final String _key)
	{
		try {
			return redisTemplate.execute(new RedisCallback<Boolean>() {
				@Override
				public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
					//当我们使用connection后，程序会自动关闭连接池
					//把对象序列化后，方可存入到redis中
					StringRedisSerializer sr=new StringRedisSerializer();
					byte [] key=sr.serialize(_key);
					byte [] value=sr.serialize("lock");
					//nx 的值必须是文件二进制流
					boolean result=connection.setNX(key,value);
					//如果3秒没有上锁成功，释放该锁
					connection.expire(key,3);
					return result;
				}
			});
		}catch (Exception ex)
		{
			return  false;
		}

	}

	/**
	 * 释放分布式锁
	 * @param key
	 */
	public void nunlock(String key)
	{
		//把redis对象序列化
		redisTemplate.setKeySerializer(new StringRedisSerializer());//设置序列化
		redisTemplate.delete(key);
	}

	/**
	 * set key and value to redis
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key,String value){
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		//设置序列化Value的实例化对象
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		ValueOperations<String,Object> vo = redisTemplate.opsForValue();
		vo.set(key, value);
		return true;
	}
	/**
	 * set key and value to redis
	 * @param key
	 * @param seconds 有效期
	 * @param value
	 * @return
	 */
	public boolean set(String key,long seconds,String value){
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		//设置序列化Value的实例化对象
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		ValueOperations<String,Object> vo = redisTemplate.opsForValue();
		vo.set(key,value);
		expire(key,seconds);
		return true;
	}
	/**
	 * 判断某个key是否存在
	 * @param key
	 * @return
	 */
	public boolean exist(String key){
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		//设置序列化Value的实例化对象
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		ValueOperations<String,Object> vo = redisTemplate.opsForValue();
		Object value=vo.get(key);
		return EmptyUtils.isEmpty(value)?false:true;
	}

	public Object get(String key) {
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		//设置序列化Value的实例化对象
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		ValueOperations<String,Object> vo = redisTemplate.opsForValue();
		return vo.get(key);
	}

	public void delete(String key){
		try{
			redisTemplate.setKeySerializer(new StringRedisSerializer());
			//设置序列化Value的实例化对象
			redisTemplate.setValueSerializer(new StringRedisSerializer());
			redisTemplate.delete(key);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Boolean setnx(final String key, final String value) throws Exception{
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisConnection){
				boolean flag=false;
				try{
					redisTemplate.setKeySerializer(new StringRedisSerializer());
					//设置序列化Value的实例化对象
					redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
					StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
					byte keys[] = stringRedisSerializer.serialize(key);
					byte values[] = stringRedisSerializer.serialize(value);
					flag=redisConnection.setNX(keys,values);
				}catch (Exception e){
					e.printStackTrace();
				}finally {
					return flag;
				}
			}
		});
	}

	public Boolean expire(final String key, final Long expireTime) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
				boolean flag=false;
				try{
					redisTemplate.setKeySerializer(new StringRedisSerializer());
					//设置序列化Value的实例化对象
					redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
					StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
					byte keys[] =stringRedisSerializer.serialize(key);
					flag=redisConnection.expire(keys,expireTime);
				}catch (Exception e){
					e.printStackTrace();
				}
				return flag;
			}
		});
	}

	public boolean lock(String key) {
		boolean flag=false;
		try{
			String lockKey = generateLockKey(key);
			flag=setnx(lockKey,"lock");
			if(flag){
				System.out.println(expire(lockKey, Constants.Redis_Expire.DEFAULT_EXPIRE));
			}
			return flag;
		}catch (Exception e){
			e.printStackTrace();
		}
		return flag;
	}

	public Object getValueNx(String key){
		String lockKey = generateLockKey(key);
		Object object=get(lockKey);
		return object;
	}

	public void unlock(String key) {
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		//设置序列化Value的实例化对象
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		String lockKey = generateLockKey(key);
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		connection.del(lockKey.getBytes());
		connection.close();
	}

	private String generateLockKey(String key) {
		return String.format("LOCK:%s", key);
	}

    public boolean validate(String token) {
        if (!exist(token)) {// token不存在
            return false;
        }
        try {
            Date TokenGenTime;// token生成时间
            String agentMD5;
            String[] tokenDetails = token.split("-");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            TokenGenTime = formatter.parse(tokenDetails[2]);
            long passed = Calendar.getInstance().getTimeInMillis()
                    - TokenGenTime.getTime();
            if(passed>Constants.Redis_Expire.SESSION_TIMEOUT*1000)
                return false;
        } catch (ParseException e) {
            return false;
        }
        return false;
    }
}
