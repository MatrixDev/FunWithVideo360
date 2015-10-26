package dev.matrix.video360.utils;

/**
 * @author rostyslav.lesovyi
 */
public class Filter {
	private double mLastSin;
	private double mLastCos;
	private double mSmoothingFactor = .95;

	public float filter(double value) {
		value = Math.toRadians(value);
		mLastSin = mSmoothingFactor * mLastSin + (1- mSmoothingFactor) * Math.sin(value);
		mLastCos = mSmoothingFactor * mLastCos + (1- mSmoothingFactor) * Math.cos(value);
		return (float) Math.toDegrees(Math.atan2(mLastSin, mLastCos));
	}
}
