package org.raxa.module.scheduler;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.Session;
import org.raxa.module.Message.MessageTemplate;
import org.raxa.module.Message.Reminder;
import org.raxa.module.database.Alert;
import org.raxa.module.database.HibernateUtil;
import org.raxa.module.database.IvrMsg;
import org.raxa.module.variables.VariableSetter;


public class ReminderUpdate implements VariableSetter {

	public void resetReminder(String pid,String name,String preferLanguage,int alertType){
		GetJson json=new GetJson();
		if(alertType==IVR_TYPE){
			List<Reminder> reminder=json.getAlert(pid);
			MessageTemplate m=new MessageTemplate();
			if((!(reminder==null)) && reminder.size()>1){
				for(Reminder r:reminder){
					List<String> template=m.templatize(r.getrawmessage(), preferLanguage, name, pid);  //have not implemented the feature to join all alert that occur between 30 minutes interval.Must incl}
					if(!(template.equals(null) && template.size()>1)){
						r.setTemplatizeMessage(template);
						addReminderToDatabase(pid,r,alertType);
					}
				}
			}
	   }
	}
			
	public void addReminderToDatabase(String pid,Reminder r,int alertType){
		int msgId=getMsgId();int count=0;			//return the max+1 msgID
		Session session = HibernateUtil.getSessionFactory().openSession();
		Timestamp time=new Timestamp(r.getTime().getTime());
		Alert a=new Alert(r.getAlertId(),pid,alertType,msgId,time,null);a.setretryCount(0);a.setIsExecuted(false);a.setServiceInfo(null);
		session.beginTransaction();
		for(String content:r.getTemplatizeMessage()){
			IvrMsg msg=new IvrMsg(msgId,++count,content);
			int id = (Integer) session.save(msg);
			msg.setId(id);
			session.persist(msg);
		}
		session.save(a);
		session.getTransaction().commit();
		session.close();
	}
	/*
	 * Return max msgId + 1.
	 */
	public int getMsgId(){
		int maxID;
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		String hql="select max(a.ivrId) from IvrMsg a";
		List list = session.createQuery(hql).list(); 
		if((Integer)list.get(0)==null)
			maxID=0;
		else 
			maxID = ( (Integer)list.get(0) ).intValue();
		session.getTransaction().commit();
		session.close();
		return maxID+1;
	}

}
