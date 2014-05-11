package org.webcurator.domain;

import java.util.List;

import org.webcurator.core.notification.InTrayResource;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Task;

public class MockInTrayDAO implements InTrayDAO {

	public MockInTrayDAO(String filename) {
		// TODO Auto-generated constructor stub
	}

	public void claimTask(User user, Task task) {
		// TODO Auto-generated method stub

	}

	public void unclaimTask(User user, Task task) {
		// TODO Auto-generated method stub

	}

	public int countNotifications(Long userOid) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int countTasks(User user, List<RolePrivilege> privs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int countTasks(String messageType, InTrayResource wctResource) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(Object obj) {
		// TODO Auto-generated method stub

	}

	public void deleteNotificationsByUser(Long userOid) {
		// TODO Auto-generated method stub

	}

	public void deleteAllTasks() {
		// TODO Auto-generated method stub
	}

	public Pagination getNotifications(Long userOid, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public Task getTask(Long resourceOid, String resourceType, String taskType) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Task> getTasks(Long resourceOid, String resourceType, String taskType) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pagination getTasks(User user, List<RolePrivilege> privs, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object load(Class clazz, Long oid) {
		// TODO Auto-generated method stub
		return null;
	}

	public InTrayResource populateOwner(InTrayResource wctResource) {
		// TODO Auto-generated method stub
		return wctResource;
	}

	public void saveOrUpdate(Object object) {
		// TODO Auto-generated method stub

	}

}
