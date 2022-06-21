package smith.lib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import android.view.View;
import android.graphics.Typeface;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class ShowMoreView extends TextView {
	
	// >>>>>>>> variables
	private int textMaxLength = 250;
	private int textColor = 0xFF999999;
	public static final int DEFAULT_COLOR = 0xFF999999;
	private String expandedText, collapsedText, originalText;
	private String showMoreText = "ShowMore", showLessText = "ShowLess";
	private SpannableString expandedTextSpannable;
	private SpannableString collapsedTextSpannable;
	private boolean isExpanded = false;
	private ShowTextViewClickListener listener;
	
	// >>>>>>>> constructors
	public ShowMoreView(Context context) {
		super(context);
		init(context, null);
		setContentText(getText().toString());
	}
	
	public ShowMoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (attrs != null) {
			init(context, attrs);
		} else {
			setContentText(getText().toString());
		}
	}
	
	public ShowMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (attrs != null) {
			init(context, attrs);
		} else {
			setContentText(getText().toString());
		}
	}
	
	// >>>>>>>> init attrs
	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ShowMoreView, 0, 0);
		try {
			int textLength = ta.getInteger(R.styleable.ShowMoreView_maxTextLength, textMaxLength);
			setMaxTextLength(textLength);
			
			String color = ta.getString(R.styleable.ShowMoreView_showMoreColor);
			if (!color.equals(null) || !color.equals("")) {
				setShowMoreColor(Color.parseColor(color));
			} else {
				setShowMoreColor(DEFAULT_COLOR);
			}
			
			String moreText = ta.getString(R.styleable.ShowMoreView_showMoreText);
			String lessText = ta.getString(R.styleable.ShowMoreView_showLessText);
			if (!moreText.equals(null) && !lessText.equals(null)) {
				setShowMoreText(moreText, lessText);
			} else if (!moreText.equals(null) && lessText.equals(null)) {
				setShowMoreText(moreText, showLessText);
			} else if (moreText.equals(null) && !lessText.equals(null)) {
				setShowMoreText(showMoreText, lessText);
			} else {
				setShowMoreText(showLessText, showLessText);
			}
			
			setContentText(getText().toString());
			
			boolean expanded = ta.getBoolean(R.styleable.ShowMoreView_expandText, isExpanded);
			expandText(expanded);
			
		} catch (Exception e) {}
		ta.recycle();
	}
	
	// >>>>>>>> methods
	public void setOnShowTextClickedListener(ShowTextViewClickListener listener) {
		this.listener = listener;
	}
	
	public void setMaxTextLength(int maxLength) {
		textMaxLength = maxLength;
	}
	
	public void setShowMoreColor(int color) {
		textColor = color;
	}
	
	public void expandText(boolean expand) {
		if (expand) {
			isExpanded = true;
			setText(expandedTextSpannable);
		} else {
			isExpanded = false;
			setText(collapsedTextSpannable);
		}
	}
	
	public void setShowMoreText(String showMore, String showLess) {
		showMoreText = showMore;
		showLessText = showLess;
	}
	
	public boolean isExpanded() {
		return isExpanded;
	}
	
	public String getOriginalText() {
		return originalText;
	}
	
	public void toggle() {
		if (isExpanded) {
			isExpanded = false;
			setText(collapsedTextSpannable);
		} else {
			isExpanded = true;
			setText(expandedTextSpannable);
		}
	}
	
	public void setContentText(String text) {
		originalText = text;
		
		this.setMovementMethod(LinkMovementMethod.getInstance());
		// show see more
		if (originalText.length() >= textMaxLength) {
			collapsedText = originalText.substring(0, textMaxLength) + "... " + showMoreText;
			expandedText = originalText + " " + showLessText;
			
			// creating spannable strings
			collapsedTextSpannable = new SpannableString(collapsedText);
			expandedTextSpannable = new SpannableString(expandedText);
			
			collapsedTextSpannable.setSpan(clickableSpan, textMaxLength + 4, collapsedText.length(), 0);
			collapsedTextSpannable.setSpan(new StyleSpan(Typeface.NORMAL), textMaxLength + 4, collapsedText.length(), 0);
			collapsedTextSpannable.setSpan(new RelativeSizeSpan(.9f), textMaxLength + 4, collapsedText.length(), 0);
			
			expandedTextSpannable.setSpan(clickableSpan, originalText.length() + 1, expandedText.length(), 0);
			expandedTextSpannable.setSpan(new StyleSpan(Typeface.NORMAL), originalText.length() + 1, expandedText.length(), 0);
			expandedTextSpannable.setSpan(new RelativeSizeSpan(.9f), originalText.length() + 1, expandedText.length(), 0);
			
			if (isExpanded) setText(expandedTextSpannable);
			else setText(collapsedTextSpannable);
			
			setTextColor(0xFF000000);
		} else {
			// to do: don't show see more
			setText(originalText);
		}
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					if (getTag() == null || !getTag().equals("spanClicked")) {
						listener.onClicked();
					}
				}
				setTag("textClicked");
			}
		});
		
		setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (listener != null) {
					listener.onLongClicked();
				}
				setTag("textLongClicked");
				
				return false;
			}
		});
	}
	
	ClickableSpan clickableSpan = new ClickableSpan() {
		@Override
		public void onClick(View widget) {
			// to prevent toggle when long click on "show more/less"
			if (getTag() == null || !getTag().equals("textLongClicked")) {
				toggle();
				setTag("spanClicked");
			} else {
				setTag("");
			}
		}
		
		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
			ds.setColor(textColor);
		}
	};
}
