import java.util.UUID

def shellCommandOutput(command) {
    def uuid = UUID.randomUUID()
    def filename = "cmd-${uuid}"
    echo filename
    sh ("${command} > ${filename}")
    def result = readFile(filename).trim()
    sh "rm ${filename}"
    return result
}

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
    // def response = httpRequest consoleLogResponseBody: true, contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: body, url: "http://54.67.13.130:5000/v3/auth/tokens", validResponseCodes: '201'
        def response = httpRequest contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: myCreds, url: "http://54.67.13.130:5000/v3/auth/tokens", validResponseCodes: '201'
        echo "Now print Status"
        println('Status: '+response.status)
        echo "Now print Response"
        println('Response: '+response.content)
        def json = new groovy.json.JsonSlurper().parseText(response.content)
        println json.token.user.id

    }
}

// curl -s -H "X-Auth-Token: $OS_TOKEN" http://54.67.13.130:8774/v2.1/os-quota-sets/05dfdad50f004456b38ef26062e72cfe

stage('Create Tenant') {
    input "Deploy?"
    milestone()
    node {
        echo "Creating the tenant"
        def jsonText = '''
            {"token": {"issued_at": "2017-02-16T20:38:59.000000Z",
                   "audit_ids": ["4GL0zg0HRze7YsFcyOCu-w"],
                   "methods": ["password"],
                    "expires_at": "2017-02-16T21:38:59.000000Z",
                    "user": {"password_expires_at": null,
                                    "domain": {"id": "default",  "name": "Default"},
                                    "id": "e4491ebc48044884bb16d8e03345b3af",
                                    "name": "admin"}
                      }
     }
'''
        def response = httpRequest contentType: 'APPLICATION_JSON', customHeaders: {"X-Auth-Token": json.token.user.id} httpMode: 'GET', requestBody: myCreds, url: "http://54.67.13.130:8774/v2.1/os-quota-sets/05dfdad50f004456b38ef26062e72cfe", validResponseCodes: '200'
    }
}
