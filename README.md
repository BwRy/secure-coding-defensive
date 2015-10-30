# Secure Coding Defensive Project

This is the Java project for Vulgar Bandidtos (Benjamin Daschel, Jacob Leque, Lander Brandt).

## Compiling

```
mvn clean compile assembly:single
```

This will generate a .jar file with all dependencies in the `target` directory.

## Requirements

You must have maven installed in order to run this project. Use maven to install the dependencies.

## Notes

- File paths must be relative to the application directory

## Protections

- You cannot write to the application binary
- Input/output files must be contained within the application directory
- Integer overflow cannot occur
