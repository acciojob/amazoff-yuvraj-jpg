package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String , Order> orderrecord ;
    private HashMap<String , DeliveryPartner> deliveryrecord ;
    private HashMap<String , List<String>> pair ;
    private HashSet<String> notassigned ;
    public OrderRepository() {
        this.orderrecord = new HashMap<String, Order>();
        this.deliveryrecord = new HashMap<>();
        this.pair = new HashMap<String, List<String>>();
        this.notassigned = new HashSet<>();
    }
    public void addOrder(Order order){
        orderrecord.put(order.getId(),order);
        notassigned.add(order.getId());
    }
    public void addPartner(String partnerid){
        deliveryrecord.put(partnerid,new DeliveryPartner(partnerid));
    }

    public void addOrderPartnerPair(String orderid ,String partnerid){
        deliveryrecord.get(partnerid).setNumberOfOrders(deliveryrecord.get(partnerid).getNumberOfOrders()+1);
        if(pair.containsKey(partnerid)){
            List<String> list = pair.get(partnerid);
            list.add(orderid);
            notassigned.remove(orderid);
        }
        else{
            pair.put(partnerid,new ArrayList<>(Arrays.asList(orderid)));
            notassigned.remove(orderid);
        }
    }
    public Order getOrderById(String orderid){
        return orderrecord.get(orderid);
    }
    public DeliveryPartner getPartnerById(String partnerid){
        return deliveryrecord.get(partnerid);
    }
    public int getOrderCountByPartnerId(String patnerid){
        return pair.get(patnerid).size();
    }
    public List<String>  getOrdersByPartnerId(String partnerid){
        List<String> list = new ArrayList<>();
        List<String> order = pair.get(partnerid);
        for(String id : order){
            list.add(orderrecord.get(id).getId());
        }
        return list;
    }

    public List<String> getAllOrders(){
        Collection<Order> values = orderrecord.values();
        List<String> list = new ArrayList<>();
        for(Order o : values){
            list.add(o.getId());
        }
        return list;
    }
    public int getCountOfUnassignedOrders() {
        return notassigned.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int numericalTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3,5));
        int count = 0;
        for(String orderId : pair.get(partnerId)){
            if(orderrecord.get(orderId).getDeliveryTime()>numericalTime){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int latestTime = 0;
        if(pair.containsKey(partnerId)){
            for(String currOrderId : pair.get(partnerId)){
                if(orderrecord.get(currOrderId).getDeliveryTime()>latestTime){
                    latestTime = orderrecord.get(currOrderId).getDeliveryTime();
                }
            }
        }
        int hours = latestTime/60;
        int minute = latestTime%60;

        String strhours = Integer.toString(hours);
        if(strhours.length()==1){
            strhours = "0"+strhours;
        }

        String minutes = Integer.toString(minute);
        if(minutes.length()==1){
            minutes = "0" + minutes;
        }
        return strhours + ":" + minutes;

    }

    public void deletePartnerById(String partnerId){
        if(!pair.isEmpty()){
            notassigned.addAll(pair.get(partnerId));
        }
        pair.remove(partnerId);
        deliveryrecord.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        orderrecord.remove(orderId);
        if(notassigned.contains(orderId)){
            notassigned.remove(orderId);
        }
        else {
            for(List<String> listofOrderIds : pair.values()){
                listofOrderIds.remove(orderId);
            }
        }
    }


}
