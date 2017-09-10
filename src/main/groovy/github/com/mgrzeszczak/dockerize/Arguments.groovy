package github.com.mgrzeszczak.dockerize


class Arguments {

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

}
