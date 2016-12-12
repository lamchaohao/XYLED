package cn.com.hotled.xyled.bean;

/**
 * Created by Lam on 2016/12/6.
 */

public class LedCard {
    private String cardName;
    private boolean isSelected;

    public LedCard(String cardName) {
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
