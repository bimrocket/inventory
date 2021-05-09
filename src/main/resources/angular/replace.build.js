var replace = require('replace-in-file');
var buildVersion = process.argv[2];
const options = {
  files: 'src/environments/environment.dev.ts',
  from: /{BUILD_VERSION}/g,
  to: buildVersion,
  allowEmptyPaths: false,
};

const options2 = {
    files: 'src/environments/environment.pre.ts',
    from: /{BUILD_VERSION}/g,
    to: buildVersion,
    allowEmptyPaths: false,
  };

try {
  let changedFiles = replace.sync(options);
  let changedFiles2 = replace.sync(options2);
  console.log('Build version set: ' + buildVersion);
}
catch (error) {
  console.error('Error occurred:', error);
}