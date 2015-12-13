# lightblue-java-generator
Experimenting with generating lightblue metadata from java classes and vice versa.

Given this:

```java
static class User {
  private String firstName;
  private String lastName;
  private Date birthdate;
  private List<Address> addresses;
  private PhoneNumber phoneNumber;
  private PhoneNumber faxNumber;

  public String getFirstName() {
    return firstName;
  }

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

    public void setPostalCode(int postalCode) {
      this.postalCode = postalCode;
    }

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
    }

    public State getState() {
      return state;
    }

    public void setState(State state) {
      this.state = state;
    }

    static class State {
      private String code;
      private String name;

      public String getCode() {
        return code;
      }

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

    public void setAreaCode(int areaCode) {
      this.areaCode = areaCode;
    }

    public int getDigits() {
      return digits;
    }

    public void setDigits(int digits) {
      this.digits = digits;
    }
  }
}
```

Get this:

```json
{
    "access": {
        "delete": [],
        "find": [],
        "insert": [],
        "update": []
    },
    "fields": {
        "addresses": {
            "items": {
                "fields": {
                    "address1": {
                        "type": "string"
                    },
                    "address2": {
                        "type": "string"
                    },
                    "city": {
                        "type": "string"
                    },
                    "postalCode": {
                        "type": "integer"
                    },
                    "state": {
                        "fields": {
                            "code": {
                                "type": "string"
                            },
                            "name": {
                                "type": "string"
                            }
                        },
                        "type": "object"
                    },
                    "usage": {
                        "type": "string"
                    }
                },
                "type": "object"
            },
            "type": "array"
        },
        "birthdate": {
            "type": "date"
        },
        "faxNumber": {
            "fields": {
                "areaCode": {
                    "type": "integer"
                },
                "digits": {
                    "type": "integer"
                }
            },
            "type": "object"
        },
        "firstName": {
            "type": "string"
        },
        "lastName": {
            "type": "string"
        },
        "phoneNumber": {
            "fields": {
                "areaCode": {
                    "type": "integer"
                },
                "digits": {
                    "type": "integer"
                }
            },
            "type": "object"
        }
    },
    "name": "user",
    "status": null,
    "version": null
}
```

## status

- [x] simple fields
- [x] object fields
- [x] array fields
- [ ] constraints (see #1)
- [ ] references (see #3)
- [ ] versions (see #2)
- [ ] enums (see #5)
- [ ] generate java from metadata (see #4). This is lowest priority since java -> metadata is lossy
      therefore going the other direction will require a small amount of "magic". For this reason I
      think it's better to simply use Java classes as the starting point / source of truth since
      they're much easier to write, usable in other code, and retain more information.
