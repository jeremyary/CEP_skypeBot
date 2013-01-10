package jary.rules.exceptions

/***
 * custom exception type thrown when attempting to access a rule session entry point that does not exist
 *
 * @author jary
 * @since 12/15/2012
 */
class MissingEntryPointException extends RuntimeException {

    MissingEntryPointException(String entryPoint) {
        super("Session entry point does not exist: {$entryPoint}")
    }
}
