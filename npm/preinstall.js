const {closeSync, openSync} = require("fs");
const {join} = require("path");


/*
 creating a dummy file which can be linked, will be replaced with a real binary
 in postinstall. We can't download the real binary here because the dependencies
 (axios etc) are not installed yet
 */
const filePath = join(__dirname, "graphql-anonymizer")
closeSync(openSync(filePath, 'w'))
