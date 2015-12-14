# lightblue-java-generator
Experimenting with generating lightblue metadata from java classes and vice versa.

Given this:

```java
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
        "_id": {
            "constraints": {
                "identity": true
            },
            "type": "string"
        },
        "addresses": {
            "constraints": {
                "minimum": 1
            },
            "items": {
                "fields": {
                    "address1": {
                        "constraints": {
                            "required": true
                        },
                        "type": "string"
                    },
                    "address2": {
                        "type": "string"
                    },
                    "city": {
                        "constraints": {
                            "required": true
                        },
                        "type": "string"
                    },
                    "postalCode": {
                        "constraints": {
                            "required": true
                        },
                        "type": "integer"
                    },
                    "state": {
                        "constraints": {
                            "required": true
                        },
                        "fields": {
                            "code": {
                                "constraints": {
                                    "required": true
                                },
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
                    "constraints": {
                        "required": true
                    },
                    "type": "integer"
                },
                "digits": {
                    "constraints": {
                        "required": true
                    },
                    "type": "integer"
                }
            },
            "type": "object"
        },
        "firstName": {
            "constraints": {
                "minLength": 2,
                "required": true
            },
            "type": "string"
        },
        "lastName": {
            "type": "string"
        },
        "phoneNumber": {
            "fields": {
                "areaCode": {
                    "constraints": {
                        "required": true
                    },
                    "type": "integer"
                },
                "digits": {
                    "constraints": {
                        "required": true
                    },
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
- [x] constraints (see [#1](../../issues/1))
- [ ] references (see [#3](../../issues/3))
- [x] versions (see [#2](../../issues/2))
- [ ] enums (see [#5](../../issues/5))
- [ ] generators (see [#6](../../issues/6))
- [ ] generate java from metadata (see [#4](../../issues/4)). This is lowest priority since java to
      metadata is lossy therefore going the other direction will require a small amount of "magic"
      to compute that missing information. For this reason I think it's better to simply use Java
      classes as the starting point / source of truth since they're much easier to write, usable in
      other code, and retain more information.
