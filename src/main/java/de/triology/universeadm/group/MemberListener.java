/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.group;

import com.github.legman.Subscribe;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserEvent;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Singleton
public final class MemberListener
{

  private static final Logger logger = LoggerFactory.getLogger(MemberListener.class);

  private final LDAPConnectionStrategy strategy;
  private final GroupManager groupManager;

  @Inject
  public MemberListener(LDAPConnectionStrategy strategy, GroupManager groupManager)
  {
    this.strategy = strategy;
    this.groupManager = groupManager;
  }

  @Subscribe
  public void handleUserEvent(UserEvent event)
  {
    logger.trace("handle user event {}", event);
    // we are asynchronous, so we have to bind and release the connection 
    // strategy manually. TODO find a better automatically way.
    strategy.bind();
    try
    {
      doHandleUserEvent(event);
    }
    finally
    {
      strategy.release();
    }
  }

  private void doHandleUserEvent(UserEvent event)
  {
    switch (event.getType())
    {
      case CREATE:
        handleCreate(event.getEntity());
        break;
      case MODIFY:
        handleModify(event.getEntity(), event.getOldEntity());
        break;
      case REMOVE:
        handleRemove(event.getEntity());
        break;
    }
  }

  private void handleCreate(User user)
  {
    addUserToGroups(user.getUsername(), user.getMemberOf());
  }
  
  private void handleModify(User user, User oldUser)
  {
    List<String> groups = user.getMemberOf();
    List<String> oldGroups = oldUser.getMemberOf();
    
    Collection<String> removed = Collections2.filter(
       oldGroups, new DoesNotContainPredicate<>(groups)
    );
    
    addUserToGroups(user.getUsername(), groups);
    removeUserFromGroups(user.getUsername(), removed);
  }

  private void handleRemove(User user)
  {
    removeUserFromGroups(user.getUsername(), user.getMemberOf());
  }
  
  private void removeUserFromGroups(String member, Iterable<String> groupnames)
  {
    for (String groupname : groupnames)
    {
      removeUserFromGroup(member, groupname);
    }
  }
  
  private void removeUserFromGroup(String member, String groupname)
  {
    Group group = groupManager.get(groupname);
    if (group != null)
    {
      if (group.getMembers().contains(member))
      {
        group.getMembers().remove(member);
        // do not send events, because this would execute the checks twice
        groupManager.modify(group, false);
      }
      else
      {
        logger.debug("group {} does not contain member {}", groupname, member);
      }
    }
    else
    {
      logger.warn("group {} does not exists", groupname);
    }
  }
  
  private void addUserToGroups(String member, Iterable<String> groupnames)
  {
    for (String groupname : groupnames)
    {
      addUserToGroup(member, groupname);
    }
  }
  
  private void addUserToGroup(String member, String groupname)
  {
    Group group = groupManager.get(groupname);
    if (group != null)
    {
      if (!group.getMembers().contains(member))
      {
        group.getMembers().add(member);
        // do not send events, because this would execute the checks twice
        groupManager.modify(group, false);
      }
      else
      {
        logger.debug("group {} already contains member {}", groupname, member);
      }
    }
    else
    {
      logger.warn("group {} does not exists", groupname);
    }
  }
  
  private static class DoesNotContainPredicate<T> implements Predicate<T> {

    private final Collection<T> collection;

    public DoesNotContainPredicate(Collection<T> collection)
    {
      this.collection = collection;
    }
    
    @Override
    public boolean apply(T input)
    {
      return ! collection.contains(input);
    }
    
  }

}
