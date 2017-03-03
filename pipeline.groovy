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

   // def body = [
     //   auth: [
     //       identity: [
     //           methods: "password",
     //                    password: [
     //                      user: [
     //                        name: "admin",
     //                        domain: [ id: "default" ],
     //                        password: "secret"
     //                      ]
     //                    ]
     //                   ]
     //       ]
     //   ]
    // def response = httpRequest consoleLogResponseBody: true, contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: body, url: "http://54.67.13.130:5000/v3/auth/tokens", validResponseCodes: '201'
        def myresponse = httpRequest contentType: 'APPLICATION_JSON', httpMode: 'POST', url: "http://54.67.13.130:5000/v3/auth/tokens", validResponseCodes: '201'
        echo "Now print Status"
        println('Status: '+myresponse.status)
        echo "Now print Response"
        println('Response: '+myresponse.content)

def latest_sha = shellCommandOutput("""
curl -i   -H 'Content-Type: application/json'   -d '
{ "auth": {
    "identity": {
      "methods": ["password"],
      "password": {
        "user": {
          "name": "admin",
          "domain": { "id": "default" },
          "password": "secret"
 }
      }
    }
  }
}' http://54.67.13.130:5000/v3/auth/tokens ; echo
"""
        )


//            sh "curl -i -H \"Content-Type: application/json\" -d \'payload=${payload}\' ${slackURL}"
    }
}


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
        def json = new groovy.json.JsonSlurper().parseText(jsonText)
        println json.token.user.id
    }
}
