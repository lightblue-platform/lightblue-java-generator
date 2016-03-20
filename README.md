# lightblue-java-generator
Generate [lightblue](https://lightblue.io) metadata from java classes.

Given this:

```java
@Version(value = "1.0.0", changelog = "Do some stuff")
static class User {
  private String _id;
  private String firstName;
  private String lastName;
  private Date lastUpdateDate;
  private Date birthdate;
  private List<Address> addresses;
  private PhoneNumber phoneNumber;
  private PhoneNumber faxNumber;
  private Status status;

  public String get_id() {
    return _id;
  }

  @Identity
  @IntSequence(name = "userIdSequence")
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

  public Date getLastUpdateDate() {
    return lastUpdateDate;
  }

  @Required
  @CurrentTime(overwrite = true)
  public void setLastUpdateDate(Date lastUpdateDate) {
    this.lastUpdateDate = lastUpdateDate;
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

  public Status getStatus() {
    return status;
  }

  @Required
  public void setStatus(Status status) {
    this.status = status;
  }

  enum Status {
    enabled,
    @Description("Use instead of deleting users")
    disabled;
  }

  static class Address {
    private String usage;
    private String address1;
    private String address2;
    private int postalCode;
    private String city;
    private State state;
    private String uuid;

    public String getUuid() {
      return uuid;
    }

    @ElementIdentity
    @Uuid
    public void setUuid(String uuid) {
      this.uuid = uuid;
    }

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
    "entityInfo": {
        "enums": [
            {
                "annotatedValues": [
                    {
                        "description": "Use instead of deleting users",
                        "name": "disabled"
                    },
                    {
                        "description": null,
                        "name": "enabled"
                    }
                ],
                "name": "status",
                "values": [
                    "disabled",
                    "enabled"
                ]
            }
        ],
        "name": "user"
    },
    "schema": {
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
                "description": null,
                "type": "string",
                "valueGenerator": {
                    "configuration": {
                        "name": "userIdSequence"
                    },
                    "type": "IntSequence"
                }
            },
            "addresses": {
                "constraints": {
                    "minItems": 1
                },
                "description": null,
                "items": {
                    "fields": {
                        "address1": {
                            "constraints": {
                                "required": true
                            },
                            "description": null,
                            "type": "string"
                        },
                        "address2": {
                            "description": null,
                            "type": "string"
                        },
                        "city": {
                            "constraints": {
                                "required": true
                            },
                            "description": null,
                            "type": "string"
                        },
                        "postalCode": {
                            "constraints": {
                                "required": true
                            },
                            "description": null,
                            "type": "integer"
                        },
                        "state": {
                            "constraints": {
                                "required": true
                            },
                            "description": null,
                            "fields": {
                                "code": {
                                    "constraints": {
                                        "required": true
                                    },
                                    "description": null,
                                    "type": "string"
                                },
                                "name": {
                                    "description": null,
                                    "type": "string"
                                }
                            },
                            "type": "object"
                        },
                        "usage": {
                            "description": null,
                            "type": "string"
                        },
                        "uuid": {
                            "constraints": {
                                "element-identity": true
                            },
                            "description": null,
                            "type": "string",
                            "valueGenerator": {
                                "type": "UUID"
                            }
                        }
                    },
                    "type": "object"
                },
                "type": "array"
            },
            "birthdate": {
                "description": null,
                "type": "date"
            },
            "faxNumber": {
                "description": null,
                "fields": {
                    "areaCode": {
                        "constraints": {
                            "required": true
                        },
                        "description": null,
                        "type": "integer"
                    },
                    "digits": {
                        "constraints": {
                            "required": true
                        },
                        "description": null,
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
                "description": null,
                "type": "string"
            },
            "lastName": {
                "description": null,
                "type": "string"
            },
            "lastUpdateDate": {
                "constraints": {
                    "required": true
                },
                "description": null,
                "type": "date",
                "valueGenerator": {
                    "overwrite": true,
                    "type": "CurrentTime"
                }
            },
            "phoneNumber": {
                "description": null,
                "fields": {
                    "areaCode": {
                        "constraints": {
                            "required": true
                        },
                        "description": null,
                        "type": "integer"
                    },
                    "digits": {
                        "constraints": {
                            "required": true
                        },
                        "description": null,
                        "type": "integer"
                    }
                },
                "type": "object"
            },
            "status": {
                "constraints": {
                    "enum": "status",
                    "required": true
                },
                "description": null,
                "type": "string"
            }
        },
        "name": "user",
        "status": {
            "value": "active"
        },
        "version": {
            "changelog": "Do some stuff",
            "value": "1.0.0"
        }
    }
}
```

## status

- [x] simple fields
- [x] object fields
- [x] array fields
- [x] constraints (see [#1](../../issues/1))
- [ ] references (see [#3](../../issues/3))
- [x] versions (see [#2](../../issues/2))
- [x] enums (see [#5](../../issues/5))
- [x] generators (see [#6](../../issues/6))
- [x] element identity
- [ ] generate java from metadata (see [#4](../../issues/4)). This is lowest priority since java to
      metadata is lossy therefore going the other direction will require a small amount of "magic"
      to compute that missing information. For this reason I think it's better to simply use Java
      classes as the starting point / source of truth since they're much easier to write, usable in
      other code, and retain more information.
