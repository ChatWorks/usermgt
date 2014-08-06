/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import com.google.common.base.Objects;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ssdorra
 */
@XmlRootElement(name = "settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings
{
  
  @XmlElement(name = "update-service-credentials")
  private Credentials updateServiceCredentials;
  
  @XmlElement(name = "update-check-enabled")
  private boolean updateCheckEnabled;
  
  @XmlElement(name = "update-bugzilla-plugin")
  private boolean updateBugzillaPlugin;
  
  @XmlElement(name = "update-cas-server")
  private boolean updateCasServer;

  public Settings()
  {
  }

  public Settings(Credentials updateServiceCredentials, boolean updateCheckEnabled, boolean updateBugzillaPlugin, boolean updateCasServer)
  {
    this.updateServiceCredentials = updateServiceCredentials;
    this.updateCheckEnabled = updateCheckEnabled;
    this.updateBugzillaPlugin = updateBugzillaPlugin;
    this.updateCasServer = updateCasServer;
  }

  public Credentials getUpdateServiceCredentials()
  {
    return updateServiceCredentials;
  }

  public boolean isUpdateCheckEnabled()
  {
    return updateCheckEnabled;
  }

  public boolean isUpdateBugzillaPlugin()
  {
    return updateBugzillaPlugin;
  }

  public boolean isUpdateCasServer()
  {
    return updateCasServer;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(updateServiceCredentials, updateCheckEnabled, 
            updateBugzillaPlugin, updateCasServer);
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
    final Settings other = (Settings) obj;
    return Objects.equal(updateServiceCredentials, other.updateServiceCredentials)
      && Objects.equal(updateCheckEnabled, other.updateCheckEnabled)
      && Objects.equal(updateBugzillaPlugin, other.updateBugzillaPlugin)
      && Objects.equal(updateCasServer, other.updateCasServer);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("updateServiceCredentials", updateServiceCredentials)
                  .add("updateCheckEnabled", updateCheckEnabled)
                  .add("updateBugzillaPlugin", updateBugzillaPlugin)
                  .add("updateCasServer", updateCasServer)
                  .toString();
  }
 
  
  public static void main(String[] args) throws JAXBException
  {
    JAXBContext.newInstance(Settings.class).createMarshaller().marshal(new Settings(null, true, true, true), System.out);
  }
  
}
