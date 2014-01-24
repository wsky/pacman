package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.*;

// wrapper executor usage, add more log and assert here
public class NativeActivityContext extends ActivityContext {
	private BookmarkManager bookmarkManager;

	protected NativeActivityContext() {
		super();
	}

	protected NativeActivityContext(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
		super(instance, executor);
		this.initialize(instance, executor, bookmarkManager);
	}

	public void initialize(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
		super.reinitialize(instance, executor);
		this.bookmarkManager = bookmarkManager;
	}

	@Override
	public void dispose() {
		super.dispose();
		this.bookmarkManager = null;
	}

	public boolean isCancellationRequested() {
		return this.currentInstance.isCancellationRequested();
	}

	public Iterable<ActivityInstance> getChildren() {
		return this.currentInstance.getChildren();
	}

	public void abort(Exception reason) {
		this.executor.abortWorkflowInstance(reason);
	}

	public void abortChildInstance(ActivityInstance activity, Exception reason) {
		if (activity.isCompleted())
			return;

		// Helper.Assert(object.ReferenceEquals(activity.Parent, this.CurrentInstance), "只允许中止当前活动实例的子活动");

		this.executor.abortActivityInstance(activity, reason);
	}

	public void markCanceled() {
		if (!this.currentInstance.isCancellationRequested())
			throw new SecurityException("markCanceledOnlyCallableIfCancelRequested");
		this.currentInstance.markCanceled();
	}

	public void cancel() {
		this.currentInstance.baseCancel(this);
	}

	public void cancelChildren() {
		this.currentInstance.cancelChildren(this);
	}

	public void cancelChild(ActivityInstance activityInstance) {
		// Helper.Assert(activityInstance != null, "activityInstance不能为空");
		// Helper.Assert(object.ReferenceEquals(activityInstance.Parent, this.CurrentInstance), "只允许取消子活动");

		if (activityInstance.isCompleted())
			return;

		if (activityInstance.getParent().equals(this.currentInstance))
			throw new SecurityException("can only cancel direct children");

		this.executor.cancelActivity(activityInstance);
	}

	public Bookmark createBookmark(String name) {
		return this.createBookmark(name, null);
	}

	public Bookmark createBookmark(String name, BookmarkCallback callback) {
		return this.bookmarkManager.createBookmark(name, callback, this.currentInstance);
	}

	public Bookmark createBookmark() {
		return this.createBookmark((BookmarkCallback) null);
	}

	public Bookmark createBookmark(BookmarkCallback callback) {
		return this.bookmarkManager.createBookmark(callback, this.currentInstance);
	}

	public boolean removeBookmark(String name) {
		return removeBookmark(new Bookmark(name));
	}

	public boolean removeBookmark(Bookmark bookmark) {
		return this.bookmarkManager.remove(bookmark, this.currentInstance);
	}

	public void RemoveAllBookmarks() {
		this.bookmarkManager.removeAll(this.currentInstance);
	}

	public void resumeBookmark(Bookmark bookmark, Object value) {
		this.executor.scheduleCompletionBookmark(bookmark, value);
	}

	public ActivityInstance scheduleActivity(Activity activity) {
		return this.scheduleActivity(activity, null, null);
	}

	public ActivityInstance scheduleActivity(Activity activity, CompletionCallback onCompleted) {
		return this.scheduleActivity(activity, onCompleted, null);
	}

	public ActivityInstance scheduleActivity(Activity activity, FaultCallback onFaulted) {
		return this.scheduleActivity(activity, null, onFaulted);
	}

	public ActivityInstance scheduleActivity(Activity activity, CompletionCallback onCompleted, FaultCallback onFaulted) {
		CompletionBookmark completionBookmark = null;
		FaultBookmark faultBookmark = null;
		if (onCompleted != null) {
			completionBookmark = new CompletionBookmark(
					new ActivityCompletionCallbackWrapper(
							onCompleted,
							this.currentInstance));
		}
		if (onFaulted != null)
			faultBookmark = new FaultBookmark(onFaulted, this.currentInstance);
		return this.internalScheduleActivity(activity, completionBookmark, faultBookmark);
	}

	public <T> ActivityInstance scheduleActivityWithResult(
			ActivityWithResult activity, CompletionWithResultCallback<T> onCompleted) {
		return this.scheduleActivityWithResult(activity, onCompleted, null);
	}

	public <T> ActivityInstance scheduleActivityWithResult(
			ActivityWithResult activity,
			CompletionWithResultCallback<T> onCompleted,
			FaultCallback onFaulted) {
		CompletionBookmark completionBookmark = null;
		FaultBookmark faultBookmark = null;
		if (onCompleted != null)
			completionBookmark = new CompletionBookmark(
					new FuncCompletionCallbackWrapper<T>(onCompleted, this.currentInstance));
		if (onFaulted != null)
			faultBookmark = new FaultBookmark(onFaulted, this.currentInstance);
		return this.internalScheduleActivity(activity, completionBookmark, faultBookmark);
	}

	private ActivityInstance internalScheduleActivity(Activity activity,
			CompletionBookmark onCompleted,
			FaultBookmark onFaulted) {
		if (this.currentInstance.isPerformingDefaultCancelation()) {
			this.currentInstance.markCanceled();
			return ActivityInstance.createCanceledActivityInstance(activity);
		}
		return this.executor.scheduleActivity(activity, this.currentInstance, onCompleted, onFaulted);
	}
}
