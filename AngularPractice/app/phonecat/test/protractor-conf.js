exports.config = {
  allScriptsTimeout: 11000,

  specs: [
    'e2e/*.js'
  ],

  capabilities: {
    'browserName': 'chrome'
  },

  chromeOnly: true,

  baseUrl: 'http://localhost:8000/',

  framework: 'jasmine',
  
  seleniumServerJar: '../node_modules/protractor/selenium/selenium-server-standalone-2.45.0.jar',

  jasmineNodeOpts: {
    defaultTimeoutInterval: 30000
  }
};
