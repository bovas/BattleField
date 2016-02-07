exports.config = {
  allScriptsTimeout: 11000,

  specs: [
    //'*.js',
    '../e2e-tests/**/scenarios.js',
    '../e2e-tests/**/*.e2e.js',
  ],

  capabilities: {
    'browserName': 'chrome'
  },

  baseUrl: 'http://localhost:8000/app/',

  framework: 'jasmine',

  jasmineNodeOpts: {
	showColors: true, // Use colors in the command line report.
    defaultTimeoutInterval: 30000
  }
};
