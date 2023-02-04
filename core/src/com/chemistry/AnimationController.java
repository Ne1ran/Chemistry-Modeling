package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.chemistry.dto.Substance;

import static com.chemistry.ExperimentWindow.*;

public class AnimationController {
    Vector2 startPos;
    Vector2 currentPos;
    Vector2 endPos;
    Texture animatedTexture;
    boolean reachedX = false;
    boolean reachedY = false;

    public AnimationController(Vector2 startPos, Vector2 endPos, Texture texture) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.currentPos = startPos;
        this.animatedTexture = texture;
        System.out.println(startPos.x + " - " + startPos.y + "\n" + endPos.x + " - " + endPos.y);
        animationStarted = true;
    }
    public Vector2 Move (){
        if (currentPos.x > endPos.x){
            currentPos.x--;
        } else if (currentPos.x < endPos.x) {
            currentPos.x++;
        } else {
            reachedX = true;
        }

        if (currentPos.y > endPos.y){
            currentPos.y--;
        } else if (currentPos.y < endPos.y) {
            currentPos.y++;
        } else {
            reachedY = true;
        }

        System.out.println(startPos.x + " - " + startPos.y);

        if (reachedX && reachedY){
            reachedX = false;
            reachedY = false;
            animationStarted = false;
            currentPos = startPos;
            animatedSubstance = new Substance();
        }

        return new Vector2(startPos.x, startPos.y);
    }

    public AnimationController(){

    }
}
