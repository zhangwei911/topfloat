package com.apkstory.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.apkstory.R;
import com.apkstory.com.vichild.file.ListExplorer;
import com.apkstory.tabhost.MainActivity;
import com.apkstory.tools.Tools;

import static com.apkstory.tools.Tools.*;

public class TopFloatMain extends Activity {

	private class LongTimeTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return params[0];
		}

		@Override
		protected void onPostExecute(String result) {
			if (progressBar.getProgress() != 100) {
				webView.stopLoading();
				progressBar.setVisibility(View.GONE);
				showMsg(context, "网页无响应");
			}
		}

	}

	@Override
	protected void onStart() {
		checkNetwork(this);
		super.onStart();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// if (checkinstallornotadobeflashapk(context)) {
		// showMsg(context, "flash has been installed!");
		// }

		textView = (TextView) findViewById(R.id.textview);
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (pathindex == 0) {
				// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				// pathindex = 1;
				// } else {
				// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				// pathindex = 0;
				// }
				Tools.startActivity(context, MainActivity.class);
			}
		});
		webView = (WebView) findViewById(R.id.webView);
		webView1 = (WebView) findViewById(R.id.webView1);
		url_editText = (EditText) findViewById(R.id.url_editText);
		url_editText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					go_button.callOnClick();
					return true;
				}
				return false;
			}
		});
		go_button = (Button) findViewById(R.id.go_button);
		float_button = (Button) findViewById(R.id.float_button);
		Button button = (Button) findViewById(R.id.button);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ListExplorer.class);
				startActivityForResult(intent, 0);
				finish();
			}
		});
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					checkEngineInstalled();
				} catch (Exception e) {
					Log.e("viz.errors.get", "get " + e.getMessage());
				}
			}
		});
		go_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isNetworkAvailable(context)) {
					String url = url_editText.getText().toString();
					webView.loadUrl(url);
					closeTheInput();
					float_check = false;
				} else {
					checkNetwork(context);
				}
			}
		});

		WebSettings settings = webView.getSettings();

		settings.setJavaScriptEnabled(true);

		settings.setAllowFileAccess(true);

		settings.setBuiltInZoomControls(true);

		settings.setDisplayZoomControls(false);

		settings.setUseWideViewPort(true);

		settings.setLoadWithOverviewMode(true);

		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

		settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

		settings.setLoadsImagesAutomatically(true);
		settings.setAllowContentAccess(false);

		// disable content url access
		settings.setAllowContentAccess(false);

		// HTML5 API flags
		settings.setAppCacheEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);

		settings.setPluginState(WebSettings.PluginState.ON);// 设置adobe插件可用

		webView.addJavascriptInterface(new InJavaScript(), "injs");

		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// 实现下载的代码
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		// -------------------Start用于视频播放的webview---------------------------
		WebSettings settings1 = webView1.getSettings();

		settings1.setJavaScriptEnabled(true);

		settings1.setAllowFileAccess(true);

		settings1.setBuiltInZoomControls(true);

		settings1.setDisplayZoomControls(false);

		settings1.setUseWideViewPort(true);

		settings1.setLoadWithOverviewMode(true);

		settings1
				.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

		settings1.setRenderPriority(WebSettings.RenderPriority.HIGH);

		settings1.setLoadsImagesAutomatically(true);
		settings1.setAllowContentAccess(false);

		// disable content url access
		settings1.setAllowContentAccess(false);

		// HTML5 API flags
		settings1.setAppCacheEnabled(true);
		settings1.setDatabaseEnabled(true);
		settings1.setDomStorageEnabled(true);

		webView1.addJavascriptInterface(new InJavaScript(), "injs");

		// ------------------------------End用于视频播放的webview-------------------------------
		// webView.setWebViewClient(new WebViewClient() {
		//
		// @Override
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// webView.loadUrl(url);
		// url_editText.setText(url);
		//
		// if (url.startsWith("http://v.youku.com/v_show/id_")) {
		// Button code_button = (Button) findViewById(R.id.code_button);
		// code_button.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// new Thread() {
		// @Override
		// public void run() {
		// //你要执行的方法
		// String temp =
		// HttpDownloader.download(url_editText.getText().toString(),
		// getString(R.string.utf));
		// if (temp != null) {
		// youkuUrl = "http://zhangwei911.duapp.com/youku.jsp?showid_youku=" +
		// temp.substring(temp.indexOf("var playPageUrl"),
		// temp.indexOf("var videoTitle")) + "var videoTitle='';";
		// }
		// //执行完毕后给handler发送一个空消息
		// handler.sendEmptyMessage(0);
		// }
		// }.start();
		// url_editText.setText(youkuUrl);
		// }
		// });
		//
		// float_button.setVisibility(View.VISIBLE);
		// int urlLength = "http://v.youku.com/v_show/id_".length() + 1;
		// videoCode = url.substring(urlLength, url.indexOf(".html"));
		// // Toast.makeText(context, "videoCode" + videoCode,
		// Toast.LENGTH_SHORT).show();
		// } else if
		// (url.startsWith("http://m.youku.com/smartphone/detail?vid=")) {
		// float_button.setVisibility(View.VISIBLE);
		// int urlLength = "http://m.youku.com/smartphone/detail?vid=".length()
		// + 1;
		// videoCode = url.substring(urlLength);
		// // Toast.makeText(context, "videoCode" + videoCode,
		// Toast.LENGTH_SHORT).show();
		// }
		// //youkuUrl =
		// "http://static.youku.com/v1.0.0349/v/swf/qplayer.swf?VideoIDS=" +
		// videoCode +
		// "=&isAutoPlay=true&isShowRelatedVideo=false&embedid=-&showAd=0";
		// // youkuUrl =
		// "http://zhangwei911.duapp.com/youku_flash.jsp?showid_youku=" +
		// videoCode;
		// return true;
		// }
		//
		// });

		webView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getX();
				y = event.getY();
				return gd.onTouchEvent(event);
			}
		});

		webView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// 获取点击的元素
				WebView.HitTestResult mResult = webView.getHitTestResult();

				final int type = mResult.getType();
				switch (type) {
				case WebView.HitTestResult.ANCHOR_TYPE:
				case WebView.HitTestResult.SRC_ANCHOR_TYPE:
					// 点击的是链接
					if (float_check) {
						setLayout(float_button, (int) x - 20, (int) y);
						float_button.setVisibility(View.VISIBLE);
						touchUrl = mResult.getExtra();
					}
					break;

				case WebView.HitTestResult.IMAGE_TYPE:
				case WebView.HitTestResult.IMAGE_ANCHOR_TYPE:
				case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
					// 点击的是图片
					break;

				default:
					// 点击的是空白处
					break;
				}
				return true;
			}
		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains("http://")) {
					webView.loadUrl(url);
				} else if (url.contains("https://")) {
					webView.loadUrl(url);
				} else if (url.contains("ftp://")) {
					webView.loadUrl(url);
				} else if (url.contains("file:///")) {
					webView.loadUrl(url);
				} else {
					if (url.startsWith("javascript:")) {
						webView.loadUrl(url);
					} else {
						webView.loadUrl("http://" + url);
					}
				}
				url_editText.setText(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progressBar.setVisibility(View.VISIBLE);
				new LongTimeTask().execute("");
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				url_editText.setText(url);
				progressBar.setVisibility(View.GONE);
			}
		});

		webView1.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progressBar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				progressBar.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}
		});
		webView1.setWebChromeClient(new MyWebChromeClient1());

		float_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent service = new Intent();
				// service.setClass(context, TopFloatService2.class);
				// service.putExtra("data", youkuUrl);
				// startService(service);

				// 播放前需要用ak,sk来初始化BEngineManager, 播放的时候会对ak,sk进行权限认证
				// 当前您也可以到VideoViewPlayingActivity进行初始化

				Intent intent = new Intent(context,
                        VideoViewPlayingActivity.class);
				intent.setData(Uri.parse(touchUrl));
				intent.putExtra("videoUrls", videosUrl);
				intent.putExtra("ctrl", "web");
				intent.putExtra("video_name", video_name);
				startActivity(intent);
				float_button.setVisibility(View.GONE);
			}
		});
		float_button.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				float_button.setVisibility(View.GONE);
				return true;
			}
		});
		// 取得URL所帶進來的Intent物件
		Intent tIntent = this.getIntent();
		// 取得URL
		Uri myURI = tIntent.getData();
		if (myURI != null) {
			// 取得URL中的Query String參數
			webView.loadUrl(myURI.toString());
		}

		sharedPreferences = getSharedPreferences("saveurl", 0);
		POST_URL = sharedPreferences.getString("posturl", "nourl");
	}

	final class InJavaScript {
		public void runOnAndroidJavaScript(final String str) {
			handler.post(new Runnable() {
				public void run() {
					touchUrl = str;
					Intent intent = new Intent(context, FullScreen.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setData(Uri.parse(touchUrl));
					startActivity(intent);
				}
			});
		}

		public void runOnAndroidJavaScript2(final String str) {
			handler.post(new Runnable() {
				public void run() {
					String[] nametemp = str.split("@name@");
					video_name = nametemp[0];
					String[] temp = nametemp[1].split("@viz@");
					videosUrl = new String[temp.length];
					for (int i = 0; i < videosUrl.length; i++) {
						videosUrl[i] = Uri.parse(temp[i]).toString();
					}
					touchUrl = videosUrl[0];
					webView.goBack();
					float_button.callOnClick();
				}
			});
		}
	}

	// 定义Handler对象
	Handler handler = new Handler() {
		@Override
		// 当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 处理UI
		}
	};

	public void closeTheInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(TopFloatMain.this
				.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

    private void playVideo(){
        try {
            webView.stopLoading();
            if (POST_URL.equals("nourl")) {
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(
                        R.layout.alertdialog_text, null);
                new AlertDialog.Builder(context)
                        .setTitle(R.string.seturl)
                        .setView(textEntryView)
                        .setPositiveButton(R.string.seturl,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        EditText et = (EditText) textEntryView
                                                .findViewById(R.id.posturl_edit);
                                        if (!TextUtils.isEmpty(et.getText()
                                                .toString())) {
                                            SharedPreferences.Editor editor = sharedPreferences
                                                    .edit();
                                            editor.putString("posturl", et
                                                    .getText().toString());
                                            editor.commit();
                                            POST_URL = et.getText()
                                                    .toString();
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {

                                    }
                                }).create().show();
            } else {
                webView1.loadUrl(POST_URL
                        + url_editText
                        .getText()
                        .toString()
                        .substring(
                                0,
                                url_editText.getText().toString()
                                        .lastIndexOf("?") > -1 ? url_editText
                                        .getText().toString()
                                        .lastIndexOf("?")
                                        : url_editText.getText()
                                        .toString()
                                        .length()));
                float_check = true;
            }
        } catch (Exception e) {
            Log.e("viz.errors", e.getMessage());
        }
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			textView.setText(getString(R.string.h));
		} else {
			textView.setText(getString(R.string.v));
		}
		super.onConfigurationChanged(newConfig);
	}


	/**
	 * 检测engine是否安装,如果没有安装需要安装engine
	 */
	private void checkEngineInstalled() {


	}

	private void setInfo(String info) {
		Message msg = new Message();
		msg.what = UPDATE_INFO;
		msg.obj = info;
		mUIHandler.sendMessage(msg);
	}

	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_INFO:
				textView.setText((String) msg.obj);
				break;
			default:
				break;
			}
		}
	};// 返回值对应的含义
	String[] mRetInfo = new String[] { "RET_NEW_PACKAGE_INSTALLED",
			"RET_NO_NEW_PACKAGE", "RET_STOPPED", "RET_CANCELED",
			"RET_FAILED_STORAGE_IO", "RET_FAILED_NETWORK",
			"RET_FAILED_ALREADY_RUNNING", "RET_FAILED_OTHERS",
			"RET_FAILED_ALREADY_INSTALLED", "RET_FAILED_INVALID_APK" };

	class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			videosUrl = message.split("@viz@");

			new AlertDialog.Builder(context)
					.setTitle("AlertDialog")
					.setMessage(message)

					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									result.confirm();
								}
							}).setCancelable(false).show();
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			new AlertDialog.Builder(context)
					.setTitle("ConfirmDialog")
					.setMessage(message)
					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									result.confirm();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									result.confirm();
								}
							}).setCancelable(false).show();
			return true;
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, final JsPromptResult result) {

			final LayoutInflater factory = LayoutInflater.from(context);

			final View dialogView = factory.inflate(R.layout.prompt_view, null);

			((TextView) dialogView.findViewById(R.id.text)).setText(message);

			((EditText) dialogView.findViewById(R.id.edit))
					.setText(defaultValue);

			new AlertDialog.Builder(context)
					.setTitle("PromptDialog")

					.setView(dialogView)
					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {

									String value = ((EditText) dialogView
											.findViewById(R.id.edit)).getText()
											.toString();
									result.confirm();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									result.confirm();
								}
							}).show();
			return true;
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			progressBar.setProgress(newProgress);
		}
	}

	class MyWebChromeClient1 extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			progressBar.setProgress(newProgress);
			super.onProgressChanged(view, newProgress);
		}
	}

	/*
	 * 设置控件所在的位置YY，并且不改变宽高， XY为绝对位置
	 */
	public static void setLayout(View view, int x, int y) {
		ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(
				view.getLayoutParams());
		margin.setMargins(x, y, x + margin.width, y + margin.height);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0) {
			String value = data.getStringExtra("data");
			textView.setText(value);
			temp = value;
			button2.setEnabled(true);
		} else {
			webView.loadUrl(data.getDataString());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		webView.pauseTimers();
		webView.clearCache(true);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		saveUrl = webView.getUrl();
		webView.pauseTimers();
		super.onPause();
	}

	@Override
	protected void onResume() {
		webView.resumeTimers();
		super.onResume();
	}

	private GestureDetector gd = new GestureDetector(
			new GestureDetector.OnGestureListener() {
				private static final int SWIPE_MIN_DISTANCE = 120;
				private static final int SWIPE_THRESHOLD_VELOCITY = 200;

				@Override
				public boolean onDown(MotionEvent motionEvent) {
					return false;
				}

				@Override
				public void onShowPress(MotionEvent motionEvent) {

				}

				@Override
				public boolean onSingleTapUp(MotionEvent motionEvent) {
					return false;
				}

				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					return false;
				}

				@Override
				public void onLongPress(MotionEvent motionEvent) {
					// 获取点击的元素
					WebView.HitTestResult mResult = webView.getHitTestResult();
					try {
						final int type = mResult.getType();
						switch (type) {
						case WebView.HitTestResult.ANCHOR_TYPE:
						case WebView.HitTestResult.SRC_ANCHOR_TYPE:
							// 点击的是链接
							if (float_check) {
								setLayout(float_button, (int) x - 20, (int) y);
								float_button.setVisibility(View.VISIBLE);
								touchUrl = mResult.getExtra();
							}
							break;

						case WebView.HitTestResult.IMAGE_TYPE:
						case WebView.HitTestResult.IMAGE_ANCHOR_TYPE:
						case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
							// 点击的是图片
							break;

						default:
							// 点击的是空白处
							break;
						}
					} catch (Exception e) {
						Log.e("viz.errors", e.getMessage());
					}
				}

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						// left左移
						Log.i("GestureDemo", "left");
						if (webView.canGoForward()) {
							webView.goForward();
						}
						return true;
					} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						// right右移
						Log.i("GestureDemo", "right");
						if (webView.canGoBack()) {
							webView.goBack();
						}
						return true;
					} else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						// up上一行
						Log.i("GestureDemo", "up");

						return true;
					} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						// down下一行
						Log.i("GestureDemo", "down");

						return true;
					}
					return false;
				}
			});

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, R.string.exit);
		menu.add(0, 2, 2, R.string.seturl);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			finish();
			break;
		case 2:
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.alertdialog_text, null);
			new AlertDialog.Builder(context)
					.setTitle(R.string.seturl)
					.setView(textEntryView)
					.setPositiveButton(R.string.seturl,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText et = (EditText) textEntryView
											.findViewById(R.id.posturl_edit);
									if (!TextUtils.isEmpty(et.getText()
											.toString())) {
										SharedPreferences.Editor editor = sharedPreferences
												.edit();
										editor.putString("posturl", et
												.getText().toString());
										editor.commit();
										POST_URL = et.getText().toString();
									}
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private TextView textView;
	private EditText url_editText;
	private String temp;
	private String POST_URL = "http://zwei.sinaapp.com/post.jsp?kw=";
	private Button button2, go_button, float_button, button3;
	private WebView webView, webView1;
	private ProgressBar progressBar;
	private String videoCode, touchUrl, youkuUrl, saveUrl, video_name;
	// 您的ak
	private String AK = "uELYiE9kdatIj0uhMWFz0sVi";
	// 您的sk的前16位
	private String SK = "hLKfXlV6nqLQbwab";
	private float x, y;
	private boolean getStrings, float_check;
	private String[] videosUrl;
	private int pathindex = 0;
	private Context context = TopFloatMain.this;
	private final int UPDATE_INFO = 0;
	private SharedPreferences sharedPreferences;
}