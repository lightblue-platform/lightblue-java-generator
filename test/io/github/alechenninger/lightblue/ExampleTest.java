package io.github.alechenninger.lightblue;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.redhat.lightblue.metadata.EntitySchema;
import com.redhat.lightblue.metadata.parser.Extensions;
import com.redhat.lightblue.metadata.parser.JSONMetadataParser;
import com.redhat.lightblue.metadata.types.DefaultTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RunWith(JUnit4.class)
public class ExampleTest {

  private Extensions extensions = new Extensions();
  private JsonNodeFactory factory = JsonNodeFactory.withExactBigDecimals(true);
  private JSONMetadataParser parser = new JSONMetadataParser(extensions, new DefaultTypes(), factory);
  private ObjectMapper mapper = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    extensions.addDefaultExtensions();
  }

  @Test
  public void demonstrateJavaToSchema() throws IOException {
    SchemaGenerator generator = new SchemaGenerator(new JavaBeansBeanReader());

    EntitySchema schema = generator.getSchema(User.class);

    assertEquals(mapper.readTree("{\n"
        + "    \"access\": {\n"
        + "        \"delete\": [],\n"
        + "        \"find\": [],\n"
        + "        \"insert\": [],\n"
        + "        \"update\": []\n"
        + "    },\n"
        + "    \"fields\": {\n"
        + "        \"_id\": {\n"
        + "            \"constraints\": {\n"
        + "                \"identity\": true\n"
        + "            },\n"
        + "            \"type\": \"string\"\n"
        + "        },\n"
        + "        \"addresses\": {\n"
        + "            \"constraints\": {\n"
        + "                \"minimum\": 1\n"
        + "            },\n"
        + "            \"items\": {\n"
        + "                \"fields\": {\n"
        + "                    \"address1\": {\n"
        + "                        \"constraints\": {\n"
        + "                            \"required\": true\n"
        + "                        },\n"
        + "                        \"type\": \"string\"\n"
        + "                    },\n"
        + "                    \"address2\": {\n"
        + "                        \"type\": \"string\"\n"
        + "                    },\n"
        + "                    \"city\": {\n"
        + "                        \"constraints\": {\n"
        + "                            \"required\": true\n"
        + "                        },\n"
        + "                        \"type\": \"string\"\n"
        + "                    },\n"
        + "                    \"postalCode\": {\n"
        + "                        \"constraints\": {\n"
        + "                            \"required\": true\n"
        + "                        },\n"
        + "                        \"type\": \"integer\"\n"
        + "                    },\n"
        + "                    \"state\": {\n"
        + "                        \"constraints\": {\n"
        + "                            \"required\": true\n"
        + "                        },\n"
        + "                        \"fields\": {\n"
        + "                            \"code\": {\n"
        + "                                \"constraints\": {\n"
        + "                                    \"required\": true\n"
        + "                                },\n"
        + "                                \"type\": \"string\"\n"
        + "                            },\n"
        + "                            \"name\": {\n"
        + "                                \"type\": \"string\"\n"
        + "                            }\n"
        + "                        },\n"
        + "                        \"type\": \"object\"\n"
        + "                    },\n"
        + "                    \"usage\": {\n"
        + "                        \"type\": \"string\"\n"
        + "                    }\n"
        + "                },\n"
        + "                \"type\": \"object\"\n"
        + "            },\n"
        + "            \"type\": \"array\"\n"
        + "        },\n"
        + "        \"birthdate\": {\n"
        + "            \"type\": \"date\"\n"
        + "        },\n"
        + "        \"faxNumber\": {\n"
        + "            \"fields\": {\n"
        + "                \"areaCode\": {\n"
        + "                    \"constraints\": {\n"
        + "                        \"required\": true\n"
        + "                    },\n"
        + "                    \"type\": \"integer\"\n"
        + "                },\n"
        + "                \"digits\": {\n"
        + "                    \"constraints\": {\n"
        + "                        \"required\": true\n"
        + "                    },\n"
        + "                    \"type\": \"integer\"\n"
        + "                }\n"
        + "            },\n"
        + "            \"type\": \"object\"\n"
        + "        },\n"
        + "        \"firstName\": {\n"
        + "            \"constraints\": {\n"
        + "                \"minLength\": 2,\n"
        + "                \"required\": true\n"
        + "            },\n"
        + "            \"type\": \"string\"\n"
        + "        },\n"
        + "        \"lastName\": {\n"
        + "            \"type\": \"string\"\n"
        + "        },\n"
        + "        \"phoneNumber\": {\n"
        + "            \"fields\": {\n"
        + "                \"areaCode\": {\n"
        + "                    \"constraints\": {\n"
        + "                        \"required\": true\n"
        + "                    },\n"
        + "                    \"type\": \"integer\"\n"
        + "                },\n"
        + "                \"digits\": {\n"
        + "                    \"constraints\": {\n"
        + "                        \"required\": true\n"
        + "                    },\n"
        + "                    \"type\": \"integer\"\n"
        + "                }\n"
        + "            },\n"
        + "            \"type\": \"object\"\n"
        + "        }\n"
        + "    },\n"
        + "    \"name\": \"user\",\n"
        + "    \"status\": {\"value\":\"active\"},\n"
        + "    \"version\": null\n"
        + "}"), parser.convert(schema));
  }

  static class User {
    private String _id;
    private String firstName;
    private String lastName;
    private Date birthdate;
    private List<Address> addresses;
    private PhoneNumber phoneNumber;
    private PhoneNumber faxNumber;

    public String get_id() {
      return _id;
    }

    @Identity
    public void set_id(String _id) {
      this._id = _id;
    }

    public String getFirstName() {
      return firstName;
    }

    @Required
    @MinLength(2)
    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public Date getBirthdate() {
      return birthdate;
    }

    public void setBirthdate(Date birthdate) {
      this.birthdate = birthdate;
    }

    public List<Address> getAddresses() {
      return addresses;
    }

    @MinItems(1)
    public void setAddresses(List<Address> addresses) {
      this.addresses = addresses;
    }

    public PhoneNumber getPhoneNumber() {
      return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
      this.phoneNumber = phoneNumber;
    }

    public PhoneNumber getFaxNumber() {
      return faxNumber;
    }

    public void setFaxNumber(PhoneNumber faxNumber) {
      this.faxNumber = faxNumber;
    }

    static class Address {
      private String usage;
      private String address1;
      private String address2;
      private int postalCode;
      private String city;
      private State state;

      public String getUsage() {
        return usage;
      }

      public void setUsage(String usage) {
        this.usage = usage;
      }

      public String getAddress1() {
        return address1;
      }

      @Required
      public void setAddress1(String address1) {
        this.address1 = address1;
      }

      public String getAddress2() {
        return address2;
      }

      public void setAddress2(String address2) {
        this.address2 = address2;
      }

      public int getPostalCode() {
        return postalCode;
      }

      @Required
      public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
      }

      public String getCity() {
        return city;
      }

      @Required
      public void setCity(String city) {
        this.city = city;
      }

      public State getState() {
        return state;
      }

      @Required
      public void setState(State state) {
        this.state = state;
      }

      static class State {
        private String code;
        private String name;

        public String getCode() {
          return code;
        }

        @Required
        public void setCode(String code) {
          this.code = code;
        }

        public String getName() {
          return name;
        }

        public void setName(String name) {
          this.name = name;
        }
      }
    }

    static class PhoneNumber {
      private int areaCode;
      private int digits;

      public int getAreaCode() {
        return areaCode;
      }

      @Required
      public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
      }

      public int getDigits() {
        return digits;
      }

      @Required
      public void setDigits(int digits) {
        this.digits = digits;
      }
    }
  }
}
