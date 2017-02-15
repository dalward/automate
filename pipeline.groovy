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

stage('Build') {
    // The first milestone step starts tracking concurrent build order
    milestone()
    node {
        echo "Building"
// Add whichever params you think you'd most want to have
// replace the slackURL below with the hook url provided by
// slack when you configure the webhook
//        #! def notifySlack(text, channel) {
//        def notifySlack() {
//            def slackURL = 'http://54.67.13.130:5000/v3/auth/tokens'
//             def payload = JsonOutput.toJson([auth      : text,
//            channel   : channel,
//                                             username  : "jenkins",
//                                             icon_emoji: ":jenkins:"])
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


stage('Deploy') {
    input "Deploy?"
    milestone()
    node {
        echo "Deploying"
    }
}
