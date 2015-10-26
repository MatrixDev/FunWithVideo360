package dev.matrix.video360;

import android.os.Bundle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import dev.matrix.video360.utils.Filter;
import dev.matrix.video360.utils.Utils;

/**
 * @author rostyslav.lesovyi
 */
public class GdxActivity extends AndroidApplication implements ApplicationListener {

	private VideoPlayer mPlayer;

	private Filter mYaw = new Filter();
	private Filter mRoll = new Filter();
	private Filter mPitch = new Filter();

	private Texture mAppLogo;
	private SpriteBatch mBatch;

	private Mesh mMesh;
	private ShaderProgram mShader;
	private OrthographicCamera mCamera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useCompass = true;
		config.useAccelerometer = true;

		initialize(this, config);
	}

	@Override
	public void create() {
		mPlayer = new VideoPlayer(this);
		mCamera = new OrthographicCamera();
		mShader = new ShaderProgram(
				Utils.read(getResources().openRawResource(R.raw.shader_v)),
				Utils.read(getResources().openRawResource(R.raw.shader_f))
		);

		mBatch = new SpriteBatch();
		mAppLogo = new Texture("ic_logo_app.png");

		MeshBuilder meshBuilder = new MeshBuilder();
		meshBuilder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates, GL20.GL_TRIANGLES);
		meshBuilder.sphere(2, 2, 2, 100, 100);
		mMesh = meshBuilder.end();
	}

	@Override
	public void resize(int width, int height) {
		mCamera.zoom = .75f;
		mCamera.setToOrtho(false, width / (float) height, 1);
		mCamera.position.setZero();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateCamera();
		mPlayer.updateTexture().bind(0);

		mShader.begin();
		mShader.setUniformi("u_texture", 0);
		mShader.setUniformMatrix("u_MVP", mCamera.combined);
		mMesh.render(mShader, GL20.GL_TRIANGLES);
		mShader.end();

		mBatch.begin();
		mBatch.draw(mAppLogo, 0, 0);
		mBatch.end();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		mPlayer.release();
	}

	private void updateCamera() {
		float yaw = mYaw.filter(-Gdx.input.getAzimuth());
		float roll = mRoll.filter(-Gdx.input.getPitch());
		float pitch = mPitch.filter(-Gdx.input.getRoll() - 90);

		pitch = Math.min(pitch, 45);
		pitch = Math.max(pitch, -45);

		mCamera.direction.x = 0;
		mCamera.direction.y = 0;
		mCamera.direction.z = 1;
		mCamera.up.x = 0;
		mCamera.up.y = 1;
		mCamera.up.z = 0;
		mCamera.position.x = 0;
		mCamera.position.y = 0;
		mCamera.position.z = 0;
		mCamera.update();

		mCamera.rotate(yaw, 0, 1, 0);
		Vector3 pivot = mCamera.direction.cpy().crs(mCamera.up);
		mCamera.rotate(pitch, pivot.x, pivot.y, pivot.z);
		mCamera.rotate(roll, mCamera.direction.x, mCamera.direction.y, mCamera.direction.z);
		mCamera.update();
	}
}
