package infnet.gads.joaolfaria.camerafilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PHOTO_CODE = 71;

    ImageView photoView;
    Bitmap original;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoView = (ImageView) findViewById(R.id.photoView);
    }

    public Bitmap invertBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap resultBitmap = Bitmap.createBitmap(w, h, bitmap.getConfig());
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                resultBitmap.setPixel(i, j, bitmap.getPixel(w - i - 1, j));
            }
        }
        return resultBitmap;
    }

    public Bitmap grayScale(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap resultBitmap = Bitmap.createBitmap(w, h, bitmap.getConfig());
        int[] pixels = new int[w * h];
        int[] intensity = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++){
                int color = pixels[i*w + j];
                int A = (color >> 24) & 0xff;
                int R = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int B = (color      ) & 0xff;
                R = (int) (R * 0.299);
                G = (int) (G * 0.587);
                B = (int) (B * 0.114);
                int lum = R+G+B;

                intensity[i*w + j] = (A & 0xff) << 24 | (lum & 0xff) << 16 | (lum & 0xff) << 8 | (lum & 0xff);
            }
        }
        return resultBitmap;
    }

    public void applyFilter(View v) {
        Button btn = (Button) v;
        Bitmap resultBitmap = original;
        switch (btn.getId()) {
            case R.id.original:
                break;
            case R.id.invert:
                resultBitmap = invertBitmap(original);
                break;
            case R.id.grayscale:
                resultBitmap = grayScale(original);
                break;
        }

        photoView.setImageBitmap(resultBitmap);

    }

    public void takePicture(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_PHOTO_CODE);
        } else {
            Log.d("DEBUG", "SEM CAMERA");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHOTO_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            original = (Bitmap) bundle.get("data");
            photoView.setImageBitmap(original);
        }
    }
}
