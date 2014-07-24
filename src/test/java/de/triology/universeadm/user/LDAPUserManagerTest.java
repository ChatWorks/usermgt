/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.LDAPHasher;
import de.triology.universeadm.PagedResultList;
import de.triology.universeadm.validation.Validator;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;
import sonia.junit.ldap.LDAP;
import sonia.junit.ldap.LDAPUnit;

/**
 *
 * @author ssdorra
 */
public class LDAPUserManagerTest
{

  private static final String BASEDN = "dc=hitchhiker,dc=com";
  private static final String LDIF_001 = "/de/triology/scmmu/usermgm/user/test.001.ldif";
  private static final String LDIF_002 = "/de/triology/scmmu/usermgm/user/test.002.ldif";
  private static final String LDIF_003 = "/de/triology/scmmu/usermgm/user/test.003.ldif";

  private EventBus eventBus;
  private Validator validator;
  private final LDAPHasher hasher = new PlainLDAPHasher();

  @Before
  public void before()
  {
    eventBus = mock(EventBus.class);
    validator = mock(Validator.class);
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void testCreate() throws LDAPException
  {
    LDAPUserManager manager = createUserManager();
    User user = createUser();
    manager.create(user);

    Entry entry = ldap.getConnection().getEntry("uid=dent,ou=People,dc=hitchhiker,dc=com");
    assertEntry(entry);
    UserEvent event = new UserEvent(user, EventType.CREATE);
    verify(eventBus, times(1)).post(event);
  }
  
  @Test(expected = UserAlreadyExistsException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void testCreateAlreadyExists() throws LDAPException
  {
    LDAPUserManager manager = createUserManager();
    User user = createUser();
    manager.create(user);
    manager.create(user);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  public void testGet() throws LDAPException{
    LDAPUserManager manager = createUserManager();
    User user = manager.get("dent");
    assertUser(user);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void testNotFound() throws LDAPException{
    LDAPUserManager manager = createUserManager();
    User user = manager.get("dent");
    assertNull(user);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  public void testRemove() throws LDAPException{
    LDAPUserManager manager = createUserManager();
    User user = manager.get("dent");
    assertNotNull(user);
    manager.remove(user);
    Entry entry = ldap.getConnection().getEntry("uid=dent,ou=People,dc=hitchhiker,dc=com");
    assertNull(entry);
    UserEvent event = new UserEvent(user, EventType.REMOVE);
    verify(eventBus, times(1)).post(event);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  public void testModify() throws LDAPException{
    LDAPUserManager manager = createUserManager();
    User user = manager.get("dent");
    assertNotNull(user);
    user.setCommonname("Dent, Arthur");
    manager.modify(user);
    Entry entry = ldap.getConnection().getEntry("uid=dent,ou=People,dc=hitchhiker,dc=com");
    assertNotNull(entry);
    assertEquals("Dent, Arthur", entry.getAttributeValue("cn"));
    UserEvent event = new UserEvent(user, EventType.MODIFY);
    verify(eventBus, times(1)).post(event);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void testGetAll() throws LDAPException {
    LDAPUserManager manager = createUserManager();
    List<User> users = manager.getAll();
    assertNotNull(users);
    assertEquals(2, users.size());
    assertUser(users.get(0));
    assertEquals("tricia", users.get(1).getUsername());
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void testGetAllPaging() throws LDAPException {
    LDAPUserManager manager = createUserManager();
    PagedResultList<User> users = manager.getAll(0, 1);
    assertNotNull(users);
    assertEquals(0, users.getStart());
    assertEquals(1, users.getLimit());
    assertEquals(2, users.getTotalEntries());
    List<User> entries = users.getEntries();
    assertEquals(1, entries.size());
    assertUser(entries.get(0));
    
    users = manager.getAll(1, 1);
    assertEquals(1, users.getStart());
    assertEquals(1, users.getLimit());
    assertEquals(2, users.getTotalEntries());
    entries = users.getEntries();
    assertEquals(1, entries.size());
    assertEquals("tricia", entries.get(0).getUsername());
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void testSearch() throws LDAPException {
    LDAPUserManager manager = createUserManager();
    List<User> users = manager.search("tricia");
    assertNotNull(users);
    assertEquals(1, users.size());
    assertEquals("tricia", users.get(0).getUsername());
  }

  private void assertUser(User user){
    assertNotNull(user);
    assertEquals("dent", user.getUsername());
    assertEquals("Arthur Dent", user.getCommonname());
    assertEquals("Arthur", user.getGivenname());
    assertEquals("Dent", user.getSurname());
    assertEquals("arthur.dent@hitchhiker.com", user.getMail());
  }
  
  private void assertEntry(Entry entry){
    assertNotNull(entry);
    assertEquals("dent", entry.getAttributeValue("uid"));
    assertEquals("Arthur Dent", entry.getAttributeValue("cn"));
    assertEquals("Arthur", entry.getAttributeValue("givenName"));
    assertEquals("Dent", entry.getAttributeValue("sn"));
    assertEquals("arthur.dent@hitchhiker.com", entry.getAttributeValue("mail"));
    assertEquals("hitchhiker123", entry.getAttributeValue("userPassword"));
  }
  
  private User createUser(){
    return new User(
      "dent", "Arthur Dent", "Arthur", "Dent", 
      "arthur.dent@hitchhiker.com", "hitchhiker123"
    );
  }

  private LDAPUserManager createUserManager() throws LDAPException
  {
    LDAPConnectionStrategy strategy = mock(LDAPConnectionStrategy.class);
    when(strategy.get()).thenReturn(ldap.getConnection());
    LDAPConfiguration config = new LDAPConfiguration(
      "localhost", 10389, "cn=Directory Manager", 
      "manager123", "ou=People,".concat(BASEDN)
    );
    return new LDAPUserManager(strategy, config, hasher, validator, eventBus);
  }

  @Rule
  public LDAPUnit ldap = new LDAPUnit();

  private static class PlainLDAPHasher extends LDAPHasher
  {

    @Override
    public byte[] hash(String value)
    {
      return value.getBytes(Charsets.UTF_8);
    }

  }

}