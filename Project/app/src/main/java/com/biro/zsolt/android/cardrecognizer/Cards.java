package com.biro.zsolt.android.cardrecognizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by Zsolt on 2017. 05. 28..
 */

enum CardNames {
    AceOfClubs, AceOfSpades, AceOfDiamonds, AceOfHearts,
    TwoOfClubs, TwoOfSpades, TwoOfDiamonds, TwoOfHearts,
    ThreeOfClubs, ThreeOfSpades, ThreeOfDiamonds, ThreeOfHearts,
    FourOfClubs, FourOfSpades, FourOfDiamonds, FourOfHearts,
    FiveOfClubs, FiveOfSpades, FiveOfDiamonds, FiveOfHearts,
    SixOfClubs, SixOfSpades, SixOfDiamonds, SixOfHearts,
    SevenOfClubs, SevenOfSpades, SevenOfDiamonds, SevenOfHearts,
    EightOfClubs, EightOfSpades, EightOfDiamonds, EightOfHearts,
    NineOfClubs, NineOfSpades, NineOfDiamonds, NineOfHearts,
    TenOfClubs, TenOfSpades, TenOfDiamonds, TenOfHearts,
    JackOfClubs, JackOfSpades, JackOfDiamonds, JackOfHearts,
    QueenOfClubs, QueenOfSpades, QueenOfDiamonds, QueenOfHearts,
    KingOfClubs, KingOfSpades, KingOfDiamonds, KingOfHearts,
    BackOfCards
}

class Cards {
    private Context context;

    Cards(Context context) {
        this.context = context;
    }

    private Mat bitmapToMat(int resourceId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        Mat card = new Mat();
        Utils.bitmapToMat(bitmap, card);
        return card;
    }

    Mat getCard(CardNames which) {
        switch (which) {
            case AceOfClubs:
                return bitmapToMat(R.drawable.ace_of_clubs2);
            case AceOfSpades:
                return bitmapToMat(R.drawable.ace_of_spades2);
            case AceOfDiamonds:
                return bitmapToMat(R.drawable.ace_of_diamonds2);
            case AceOfHearts:
                return bitmapToMat(R.drawable.ace_of_hearts2);
            case TwoOfClubs:
                return bitmapToMat(R.drawable.two_of_clubs);
            case TwoOfSpades:
                return bitmapToMat(R.drawable.two_of_spades);
            case TwoOfDiamonds:
                return bitmapToMat(R.drawable.two_of_diamonds);
            case TwoOfHearts:
                return bitmapToMat(R.drawable.two_of_hearts);
            case ThreeOfClubs:
                return bitmapToMat(R.drawable.three_of_clubs);
            case ThreeOfSpades:
                return bitmapToMat(R.drawable.three_of_spades);
            case ThreeOfDiamonds:
                return bitmapToMat(R.drawable.three_of_diamonds);
            case ThreeOfHearts:
                return bitmapToMat(R.drawable.three_of_hearts);
            case FourOfClubs:
                return bitmapToMat(R.drawable.four_of_clubs);
            case FourOfSpades:
                return bitmapToMat(R.drawable.four_of_spades);
            case FourOfDiamonds:
                return bitmapToMat(R.drawable.four_of_diamonds);
            case FourOfHearts:
                return bitmapToMat(R.drawable.four_of_hearts);
            case FiveOfClubs:
                return bitmapToMat(R.drawable.five_of_clubs);
            case FiveOfSpades:
                return bitmapToMat(R.drawable.five_of_spades);
            case FiveOfDiamonds:
                return bitmapToMat(R.drawable.five_of_diamonds);
            case FiveOfHearts:
                return bitmapToMat(R.drawable.five_of_hearts);
            case SixOfClubs:
                return bitmapToMat(R.drawable.six_of_clubs);
            case SixOfSpades:
                return bitmapToMat(R.drawable.six_of_spades);
            case SixOfDiamonds:
                return bitmapToMat(R.drawable.six_of_diamonds);
            case SixOfHearts:
                return bitmapToMat(R.drawable.six_of_hearts);
            case SevenOfClubs:
                return bitmapToMat(R.drawable.seven_of_clubs);
            case SevenOfSpades:
                return bitmapToMat(R.drawable.seven_of_spades);
            case SevenOfDiamonds:
                return bitmapToMat(R.drawable.seven_of_diamonds);
            case SevenOfHearts:
                return bitmapToMat(R.drawable.seven_of_hearts);
            case EightOfClubs:
                return bitmapToMat(R.drawable.eight_of_clubs);
            case EightOfSpades:
                return bitmapToMat(R.drawable.eight_of_spades);
            case EightOfDiamonds:
                return bitmapToMat(R.drawable.eight_of_diamonds);
            case EightOfHearts:
                return bitmapToMat(R.drawable.eight_of_hearts);
            case NineOfClubs:
                return bitmapToMat(R.drawable.nine_of_clubs);
            case NineOfSpades:
                return bitmapToMat(R.drawable.nine_of_spades);
            case NineOfDiamonds:
                return bitmapToMat(R.drawable.nine_of_diamonds);
            case NineOfHearts:
                return bitmapToMat(R.drawable.nine_of_hearts);
            case TenOfClubs:
                return bitmapToMat(R.drawable.ten_of_clubs);
            case TenOfSpades:
                return bitmapToMat(R.drawable.ten_of_spades);
            case TenOfDiamonds:
                return bitmapToMat(R.drawable.ten_of_diamonds);
            case TenOfHearts:
                return bitmapToMat(R.drawable.ten_of_hearts);
            case JackOfClubs:
                return bitmapToMat(R.drawable.jack_of_clubs);
            case JackOfSpades:
                return bitmapToMat(R.drawable.jack_of_spades);
            case JackOfDiamonds:
                return bitmapToMat(R.drawable.jack_of_diamonds);
            case JackOfHearts:
                return bitmapToMat(R.drawable.jack_of_hearts);
            case QueenOfClubs:
                return bitmapToMat(R.drawable.queen_of_clubs);
            case QueenOfSpades:
                return bitmapToMat(R.drawable.queen_of_spades);
            case QueenOfDiamonds:
                return bitmapToMat(R.drawable.queen_of_diamonds);
            case QueenOfHearts:
                return bitmapToMat(R.drawable.queen_of_hearts);
            case KingOfClubs:
                return bitmapToMat(R.drawable.king_of_clubs);
            case KingOfSpades:
                return bitmapToMat(R.drawable.king_of_spades);
            case KingOfDiamonds:
                return bitmapToMat(R.drawable.king_of_diamonds);
            case KingOfHearts:
                return bitmapToMat(R.drawable.king_of_hearts);
            case BackOfCards:
                return bitmapToMat(R.drawable.back);
            default:
                return null;
        }
    }
}
