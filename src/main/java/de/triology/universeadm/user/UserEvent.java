/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;

import com.google.common.base.Objects;
import de.triology.universeadm.EntityEvent;
import de.triology.universeadm.EventType;

/**
 *
 * @author ssdorra
 */
public class UserEvent implements EntityEvent<User>
{
  private final User entity;
  private final EventType type;

  public UserEvent(User entity, EventType type)
  {
    this.entity = entity;
    this.type = type;
  }

  @Override
  public EventType getType()
  {
    return type;
  }

  @Override
  public User getEntity()
  {
    return entity;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(entity, type);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final UserEvent other = (UserEvent) obj;
    return Objects.equal(entity, other.entity)
            && Objects.equal(type, other.type);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
            .add("entity", entity)
            .add("type", type)
            .toString();
  }

}
