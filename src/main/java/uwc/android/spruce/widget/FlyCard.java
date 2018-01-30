package uwc.android.spruce.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import java.util.HashMap;
import java.util.WeakHashMap;

import uwc.android.spruce.R;

/**
 * Created by steven on 10/17/16.
 */

public class FlyCard extends FrameLayout {
    private final static String TAG = FlyCard.class.getSimpleName();

    public final class Event {
        Event(int id, HashMap params) {
            this.id = id;
            this.params = params;
        }

        public void fire() {
            FlyCard.this.dispatch(this);
        }

        public HashMap params;
        public int id;

        public final static int id_show_card = -1;
        public final static int id_card_dismiss = id_show_card + 1;
    }

    public interface CardFactory {

        void onAttach(FlyCard flyContainer);

        interface CardHolder {
            View onCreateView(LayoutInflater inflater);
            void onCardEvent(Event event);
        }

        CardHolder onCreateCard(int viewType);
    }

    private WeakHashMap<Integer, CardFactory.CardHolder> mCards = new WeakHashMap<>();
    private Integer mCurCard = Integer.MIN_VALUE;
    private CardFactory mCardFactory = new CardFactory() {
        @Override
        public void onAttach(FlyCard flyContainer) {

        }

        @Override
        public CardHolder onCreateCard(int viewType) {
            return null;
        }
    };

    private PopupWindow mCardHost = null;

    public FlyCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCardHost = new PopupWindow(new View(context), 1, 1);
        mCardHost.setWidth(LayoutParams.MATCH_PARENT);mCardHost.setHeight(LayoutParams.MATCH_PARENT);
        mCardHost.setBackgroundDrawable(new ColorDrawable(Color.argb(126, 0, 0, 0)));
        mCardHost.setFocusable(true);
    }

    public FlyCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlyCard(Context context) {
        this(context, null);
    }

    public void setCardFactory(CardFactory factory) throws IllegalArgumentException {
        this.mCardFactory = factory;
        if (null == mCardFactory)
            throw new IllegalArgumentException();
        mCardFactory.onAttach(this);
    }

    private void dispatch(Event event) {
        CardFactory.CardHolder cardHolder = mCards.get(mCurCard);
        switch (event.id) {
            case Event.id_card_dismiss:
                if (!mCardHost.isShowing()) break;
                mCardHost.dismiss();
                mCardHost.getContentView().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_out));
                mCardHost.setContentView(null);
                break;
            case Event.id_show_card:
                if (mCardHost.isShowing()) break;
                View card = cardHolder.onCreateView(LayoutInflater.from(getContext()));
                mCardHost.setContentView(card);
                mCardHost.showAtLocation(this, Gravity.NO_GRAVITY, 0, 0);
                card.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_in));
                break;
            default:
                break;
        }
        cardHolder.onCardEvent(event);
    }

    public Event obtainEvent(int id, HashMap params) {
        return new Event(id, params);
    }

    public void fireCard(int type, HashMap params) {
        CardFactory.CardHolder cardHolder = mCards.get(type);
        if (null == cardHolder
                && null == (cardHolder = mCardFactory.onCreateCard(type)))
            return;
        mCards.put(mCurCard = type, cardHolder);

        obtainEvent(Event.id_show_card, params).fire();
    }

    public void dismiss() {
        obtainEvent(FlyCard.Event.id_card_dismiss, null).fire();
    }
}
