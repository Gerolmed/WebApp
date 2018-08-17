package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * The class Captcha.
 */
@ApiModel(
        description = "Zur Verwendung des Captchas."
)
@Getter
public class Captcha {

    /**
     * The answer to the Captcha of the User.
     */
    @ApiModelProperty(notes = "Antwort")
    private String answer;

    /**
     * The token of the Captcha.
     */
    @ApiModelProperty(notes = "Token")
    private String imageToken;

    /**
     * Instantiates a new Captcha.
     *
     * @param answerParam        the username
     * @param imageTokenParam        the password
     */
    public Captcha(final String answerParam, final String imageTokenParam) {
        this.answer = answerParam;
        this.imageToken = imageTokenParam;
    }

    /**
     * Empty constructor for a new Captcha.
     */
    public Captcha() {
    }
}