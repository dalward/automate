
        myID = 'abc123'


        stage('Receive Tenant Input') {
            // The first milestone step starts tracking concurrent build order
            milestone()
            node {
                echo ram
                echo "Accepting tenant values"

                def myCreds = """
                    { "auth":
                        {"identity": {"methods": ["password"],
                         "password": {"user":
                                     {"name": "admin",
                                      "domain": { "id": "default" },
                                      "password": "secret"
                                      }
                                      }
                          }
                        }
                    }
                """

                def tenCreds = """
                    {"auth":{"passwordCredentials":{"username":"admin", "password":"secret"}, "tenantName":"admin"}}
                """
// used v3/auth/tokens in URL string changed to /v2/tokens

                // def response = httpRequest contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: myCreds, url: "http://54.67.13.130:5000/v3/auth/tokens", validResponseCodes: '201'


                def response = httpRequest contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: tenCreds, url: "http://54.67.13.130:5000/v2.0/tokens", validResponseCodes: '200:202'
                echo "Now print Status"
                println('Status: ' + response.status)
                echo "Now print Response"
                println('Response: ' + response.content)
                def json = new groovy.json.JsonSlurper().parseText(response.content)
                // myID = json.token.user.id
                myID = json.token.id
            }
        }

// curl -s -H "X-Auth-Token: $OS_TOKEN" http://54.67.13.130:8774/v2.1/os-quota-sets/05dfdad50f004456b38ef26062e72cfe
// customHeaders: [['X-Auth-Token': myID]],

        stage('Create Tenant') {
            input "Deploy?"
            milestone()
            node {
                println(myID)
                echo "Creating the tenant"
                def response = httpRequest customHeaders: [[name: 'X-Auth-Token', value: myID]], url: 'http://54.67.13.130:8774/v2.1/os-quota-sets/05dfdad50f004456b38ef26062e72cfe', validResponseCodes: '100:202'
                println('Status: ' + response.status)
                println('Response: ' + response.content)

            }
        }
