package cn.gy.utils;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Queue;
import javax.jms.Topic;

@Component
public class MqUtil {

    @Resource
    JmsMessagingTemplate jmsMessagingTemplatel;

    /**
     * 点对点方式
     * @param tails
     * @param message
     */
    public void sentQueme(String tails,Object message){
        Queue queue = new ActiveMQQueue(tails);
        jmsMessagingTemplatel.convertAndSend(queue,message);
    }

    /**
     * 订阅模式
     * @param tails
     * @param message
     */
    public void sentT(String tails,Object message){
        Topic topic = new ActiveMQTempTopic(tails);
        jmsMessagingTemplatel.convertAndSend(topic,message);
    }
}
