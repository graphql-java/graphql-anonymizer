const {createWriteStream} = require("fs");
const axios = require("axios");
const {join} = require("path");
const os = require("os");

const {version} = require("./package.json");

const prefix = getPlatformPrefix();
const url = `https://github.com/graphql-java/graphql-anonymizer/releases/download/v${version}/graphql-anonymizer-${version}-${prefix}`;

axios({url, responseType: "stream"})
    .then(res => {
        return res.data.pipe(createWriteStream(join(__dirname, "graphql-anonymizer")));
    })
    .then(() => {
        console.log(`graphql-anonymizer has been installed!`);
    })
    .catch(e => {
        console.log(e);
        process.exit(1);
    });

function getPlatformPrefix() {
    const type = os.type();
    const arch = os.arch();

    if (type === 'Windows_NT' && arch === 'x64') return 'windows';
    if (type === 'Linux' && arch === 'x64') return 'linux';
    if (type === 'Darwin' && arch === 'x64') return 'mac';

    throw new Error(`Unsupported platform: ${type} ${arch}`);
}
