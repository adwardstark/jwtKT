# jwtKT

A fast cli tool to generate and parse JWTs ( Json-Web-Token ), written in Kotlin, uses [JJWT](https://github.com/jwtk/jjwt) library.

### Getting Started
 - Just import this project in your favourite IDE, ( preferred is IntelliJ )
 - Let the build sink-in
 - Go to Run > Edit configuration
 - Pass the arguments in `Program arguments` field > Hit OK
 - Go to Run > Run MainKT
 - You're all set!

### Actions
This tool accepts a number of arguments to perform different actions, to see what all are supported, type `-h or --help` in arguments to view usage, it'll look something like this:

```
  Usage: JwtKT [options]
  Options:
    -a, --algo
      Algorithm used for signing
    -c, --config
      JSON configuration file
    -d, --debug
      Enable debug logs
      Default: false
    -e, --expiry
      Set token expiration in seconds
      Default: 0
    -h, --help
      Show help usage
      Default: false
    -i, --issued
      Adds the time of token issue
      Default: false
    -k, --key
      Secret key used for signing
    -p, --parse
      Parse an existing token
    -v, --version
      Show version info
      Default: false
```

### License
Apache 2.0. See the [LICENSE](https://github.com/adwardstark/jwtKT/blob/master/LICENSE) file for details.