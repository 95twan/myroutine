package com.node5.shopservice.shop.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ShopErrorCode implements BaseErrorCode {
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "SHOP_001", "상점을 찾을 수 없습니다."),
    WALLET_REQUIRED(HttpStatus.NOT_FOUND.value(), "SHOP_002", "상점을 생성하기전 예치금을 먼저 생성해야 합니다."),
    ROLE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SHOP_003", "회원 권한 업데이트에 실패했습니다."),
    UNCAUGHT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SHOP_004", "알 수 없는 서버 에러"),
    SHOP_REGISTRATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "SHOP_005", "상점 등록 정보를 찾을 수 없습니다."),
    SHOP_DELETION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "SHOP_006", "상점 삭제 정보를 찾을 수 없습니다."),
    SHOP_DELETE_NOT_ALLOWED(HttpStatus.CONFLICT.value(), "SHOP_007", "등록 진행 중인 상점은 삭제할 수 없습니다."),
    SHOP_IS_REGISTERING(HttpStatus.BAD_REQUEST.value(), "SHOP_008", "상점이 등록 중입니다."),
    SHOP_IS_DELETING(HttpStatus.BAD_REQUEST.value(), "SHOP_009", "상점이 삭제 중입니다.");

    private final int status;
    private final String code;
    private final String message;

    ShopErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
