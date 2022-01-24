package nextstep.subway.applicaion.exception;

public class DuplicationNameException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "같은 이름으로 등록된 노선이 존재합니다.";

    public DuplicationNameException() {
        super(DEFAULT_MESSAGE);
    }
}
