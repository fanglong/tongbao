package affairs.Impl;

import affairs.OrderAffairs;
import model.Account;
import model.Order;
import model.OrderTruckType;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/**
 * Created by cg on 2016/3/8.
 */
@Repository
public class OrderAffairsImpl  implements OrderAffairs{
        //保存使用事务办法，包含订单，订单用车类型，账单，更新用户账户
        public boolean saveOrderAffairs(Order order,String [] tTypes){
            Session session = HibernateUtil.getSession();
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                session.save(order);
                Byte payType = order.getPayType();
                if(payType.equals(0)){
                    Account account = new Account();
                    account.setOrderId(order.getId());
                    account.setBuildTime(order.getBuildTime());
                    account.setUserId(order.getShipperId());
                    account.setMoney(order.getPrice());
                    account.setType(new Byte("2"));
                    session.save(account);
                    //扣钱
                    User user = (User) session.load(User.class,order.getShipperId());
                    double moneyNow = user.getMoney()-order.getPrice();
                    user.setMoney(moneyNow);
                    session.update(user);
                }
                for(int i=0;i<tTypes.length;i++){
                    OrderTruckType orderTruckType = new OrderTruckType();
                    orderTruckType.setTruckType(new Byte(tTypes[i]));
                    orderTruckType.setOrderId(order.getId());
                    session.save(orderTruckType);
                }
                tx.commit();
                return true;
            }catch (Exception e){
                if(tx!=null) tx.rollback();
                e.printStackTrace();
                return false;
            }finally {
                HibernateUtil.closeSession();
            }


        }

    @Override
    public boolean finishOrderAffairs(Order order) {
        Session session = HibernateUtil.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(order);
            Account account = new Account();
            account.setOrderId(order.getId());
            account.setBuildTime(new Timestamp(System.currentTimeMillis()));
            account.setUserId(order.getDriverId());
            account.setMoney(order.getPrice());
            account.setType(new Byte("4"));//表示到账账单
            session.save(account);
            User user = (User) session.load(User.class,order.getDriverId());
            double moneyNow = user.getMoney()+order.getPrice();
            user.setMoney(moneyNow);
            session.update(user);//到账
            tx.commit();
            return true;
        }catch (Exception e){
            if(tx!=null) tx.rollback();
            return false;
        }finally {
            HibernateUtil.closeSession();
        }
    }

    @Override
    public int cancelOrderAffairs(Order order) {
        Session session = HibernateUtil.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(order);
            Account account = new Account();
            account.setOrderId(order.getId());
            account.setBuildTime(new Timestamp(System.currentTimeMillis()));
            account.setUserId(order.getShipperId());
            account.setMoney(order.getPrice());
            account.setType(new Byte("3"));//表示退款账单
            session.save(account);
            User user = (User) session.load(User.class,order.getShipperId());
            double moneyNow = user.getMoney()+order.getPrice();
            user.setMoney(moneyNow);
            session.update(user);//退钱
            tx.commit();
            return 1;
        }catch (Exception e){
            if(tx!=null) tx.rollback();
            e.printStackTrace();
            return 0;
        }finally {
            HibernateUtil.closeSession();
        }

    }

    //线上支付且订单尚未被抢,使用事务办法，包含退款账单，把钱退回用户账号
    @Override
    public boolean deleteOrderAffairs(Order order) {
        Session session = HibernateUtil.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(order);
            Account account = new Account();
            account.setOrderId(order.getId());
            account.setBuildTime(new Timestamp(System.currentTimeMillis()));
            account.setUserId(order.getShipperId());
            account.setMoney(order.getPrice());
            account.setType(new Byte("3"));//表示退款账单
            session.save(account);
            User user = (User) session.load(User.class,order.getShipperId());
            double moneyNow = user.getMoney()+order.getPrice();
            user.setMoney(moneyNow);
            session.update(user);//退钱
            tx.commit();
            return true;
        }catch (Exception e){
            if(tx!=null) tx.rollback();
            return false;
        }finally {
            HibernateUtil.closeSession();
        }
    }

}