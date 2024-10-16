package com.lody.virtual.server.job;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.text.TextUtils;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VJobScheduler;
import com.lody.virtual.client.stub.StubManifest;
import com.lody.virtual.helper.utils.Singleton;
import com.lody.virtual.os.VEnvironment;
import com.lody.virtual.remote.VJobWorkItem;
import com.lody.virtual.server.interfaces.IJobService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Lody
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VJobSchedulerService extends IJobService.Stub {

    private static final String TAG = VJobScheduler.class.getSimpleName();

    private static final int SINGLE_MAX_JOB = 30;//单个应用最大的Jobservice
    private static final int MAX_JOB = 90;//所有应用最大的Jobservice

    private static final int JOB_FILE_VERSION = 1;
    private volatile Map<JobId, JobConfig> mJobStore = new ConcurrentHashMap<>();

    private int mNextJobId = 1;

    private final JobScheduler mScheduler = (JobScheduler)
            VirtualCore.get().getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);

    private final ComponentName mJobProxyComponent;

    private VJobSchedulerService() {
        mJobProxyComponent = new ComponentName(VirtualCore.get().getHostPkg(), StubManifest.STUB_JOB);
        readJobs();
    }

    private static final Singleton<VJobSchedulerService> gDefault = new Singleton<VJobSchedulerService>() {
        @Override
        protected VJobSchedulerService create() {
            return new VJobSchedulerService();
        }
    };

    public static VJobSchedulerService get() {
        return gDefault.get();
    }


    public static final class JobId implements Parcelable {
        @Override
        public String toString() {
            return "JobId{" +
                    "vuid=" + vuid +
                    ", packageName='" + packageName + '\'' +
                    ", clientJobId=" + clientJobId +
                    '}';
        }

        public int vuid;
        public String packageName;
        /**
         * The id given by User.
         */
        public int clientJobId;

        JobId(int vuid, String packageName, int id) {
            this.vuid = vuid;
            this.packageName = packageName;
            this.clientJobId = id;
        }


        JobId(Parcel in) {
            this.vuid = in.readInt();
            this.packageName = in.readString();
            this.clientJobId = in.readInt();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            JobId jobId = (JobId) o;

            return vuid == jobId.vuid
                    && clientJobId == jobId.clientJobId
                    && TextUtils.equals(packageName, jobId.packageName);
        }

        @Override
        public int hashCode() {
            int result = vuid;
            result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
            result = 31 * result + clientJobId;
            return result;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.vuid);
            dest.writeString(this.packageName);
            dest.writeInt(this.clientJobId);
        }

        public static final Creator<JobId> CREATOR = new Creator<JobId>() {
            @Override
            public JobId createFromParcel(Parcel source) {
                return new JobId(source);
            }

            @Override
            public JobId[] newArray(int size) {
                return new JobId[size];
            }
        };
    }

    public static final class JobConfig implements Parcelable {

        /**
         * The id given by VA.
         */
        public int virtualJobId;
        public String serviceName;
        public PersistableBundle extras;
        public String pkgName;
        public int intervalMillis=0;

        @Override
        public String toString() {
            return "JobConfig{" +
                    "virtualJobId=" + virtualJobId +
                    ", serviceName='" + serviceName + '\'' +
                    ", extras=" + extras +
                    ", pkgName='" + pkgName + '\'' +
                    '}';
        }

        JobConfig(int virtualJobId, String serviceName, String pkgName, PersistableBundle extra) {
            this.virtualJobId = virtualJobId;
            this.serviceName = serviceName;
            this.pkgName = pkgName;
            this.extras = extra;
        }

        JobConfig(Parcel in) {
            try {
                this.virtualJobId = in.readInt();
                this.serviceName = in.readString();
                this.pkgName = in.readString();
                this.extras = in.readParcelable(PersistableBundle.class.getClassLoader());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.virtualJobId);
            dest.writeString(this.serviceName);
            dest.writeString(this.pkgName);
            dest.writeParcelable(this.extras, flags);
        }

        public static final Creator<JobConfig> CREATOR = new Creator<JobConfig>() {
            @Override
            public JobConfig createFromParcel(Parcel source) {
                return new JobConfig(source);
            }

            @Override
            public JobConfig[] newArray(int size) {
                return new JobConfig[size];
            }
        };
    }


    @Override
    public int schedule(int uid, JobInfo job) {
        int id = job.getId();
        int schedule = JobScheduler.RESULT_FAILURE;
        ComponentName service = job.getService();
        JobId jobId = new JobId(uid, service.getPackageName(), id);
        JobConfig config;
        synchronized (mJobStore) {
            if (mJobStore.size() > MAX_JOB || countJobsForPkg(service.getPackageName()) > SINGLE_MAX_JOB) {//最大注册的数量是90,如果大于90 直接返回失败,
                return -1;
            }
            config = mJobStore.get(jobId);
            if (config == null) {
                int jid = mNextJobId;
                mNextJobId++;
                config = new JobConfig(jid, service.getClassName(), service.getPackageName(), job.getExtras());
                mJobStore.put(jobId, config);
            }
            config.serviceName = service.getClassName();
            config.extras = job.getExtras();
            mirror.android.app.job.JobInfo.jobId.set(job, config.virtualJobId);
            mirror.android.app.job.JobInfo.service.set(job, mJobProxyComponent);
            schedule = mScheduler.schedule(job);
            if (schedule == JobScheduler.RESULT_SUCCESS) {
                saveJobs();
            }
        }

        return schedule;
    }

    private void saveJobs() {
        File jobFile = VEnvironment.getJobConfigFile();
        Parcel p = Parcel.obtain();
        try {
            p.writeInt(JOB_FILE_VERSION);
            p.writeInt(mJobStore.size());
            for (Map.Entry<JobId, JobConfig> entry : mJobStore.entrySet()) {
                entry.getKey().writeToParcel(p, 0);
                entry.getValue().writeToParcel(p, 0);
            }
            FileOutputStream fos = new FileOutputStream(jobFile);
            fos.write(p.marshall());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            p.recycle();
        }
    }

    private void readJobs() {
        File jobFile = VEnvironment.getJobConfigFile();
        if (!jobFile.exists()) {
            return;
        }
        Parcel p = Parcel.obtain();
        try {
            FileInputStream fis = new FileInputStream(jobFile);
            byte[] bytes = new byte[(int) jobFile.length()];
            int len = fis.read(bytes);
            fis.close();
            if (len != bytes.length) {
                throw new IOException("Unable to read job config.");
            }
            p.unmarshall(bytes, 0, bytes.length);
            p.setDataPosition(0);
            int version = p.readInt();
            if (version != JOB_FILE_VERSION) {
                throw new IOException("Bad version of job file: " + version);
            }
            if (!mJobStore.isEmpty()) {
                mJobStore.clear();
            }
            int count = p.readInt();
            int max = 0;
            List<JobInfo> allPendingJobs = mScheduler.getAllPendingJobs();
            for (int i1 = 0; i1 < count; i1++) {
                JobId jobId = new JobId(p);
                JobConfig config = new JobConfig(p);
                if (allPendingJobs != null) {
                    for (int i = 0; i < allPendingJobs.size(); i++) {
                        if (TextUtils.equals(allPendingJobs.get(i).getId() + "", config.virtualJobId + "")
                                && (allPendingJobs.get(i).getService().getClassName().contains(StubManifest.STUB_JOB))) {
                            mJobStore.put(jobId, config);
                            max = Math.max(max, config.virtualJobId);
                        }
                    }
                }
            }
            mNextJobId = max + 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            p.recycle();
        }

    }

    @Override
    public void cancel(int uid, int jobId) {
        synchronized (mJobStore) {
            boolean changed = false;
            Iterator<Map.Entry<JobId, JobConfig>> iterator = mJobStore.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<JobId, JobConfig> entry = iterator.next();
                JobId job = entry.getKey();
                JobConfig config = entry.getValue();
                if ((uid == -1 || job.vuid == uid) && job.clientJobId == jobId) {
                    changed = true;
                    mScheduler.cancel(config.virtualJobId);
                    iterator.remove();
                    break;
                }
            }
            if (changed) {
                saveJobs();
            }
        }
    }

    @Override
    public void cancelAll(int uid) {
        synchronized (mJobStore) {
            boolean changed = false;
            Iterator<Map.Entry<JobId, JobConfig>> iterator = mJobStore.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<JobId, JobConfig> entry = iterator.next();
                JobId job = entry.getKey();
                if (job.vuid == uid) {
                    JobConfig config = entry.getValue();
                    mScheduler.cancel(config.virtualJobId);
                    changed = true;
                    iterator.remove();
                    break;
                }
            }
            if (changed) {
                saveJobs();
            }
        }
    }

    @Override
    public List<JobInfo> getAllPendingJobs(int uid) {
        List<JobInfo> jobs = mScheduler.getAllPendingJobs();
        synchronized (mJobStore) {
            Iterator<JobInfo> iterator = jobs.listIterator();
            while (iterator.hasNext()) {
                JobInfo job = iterator.next();
                if (!StubManifest.STUB_JOB.equals(job.getService().getClassName())) {
                    // Schedule by Host, invisible in VA.
                    iterator.remove();
                    continue;
                }
                Map.Entry<JobId, JobConfig> jobEntry = findJobByVirtualJobId(job.getId());
                if (jobEntry == null) {
                    mScheduler.cancel(job.getId());
                    iterator.remove();
                    continue;
                }
                JobId jobId = jobEntry.getKey();
                JobConfig config = jobEntry.getValue();
                if (jobId.vuid != uid) {
                    iterator.remove();
                    continue;
                }
                mirror.android.app.job.JobInfo.jobId.set(job, jobId.clientJobId);
                mirror.android.app.job.JobInfo.service.set(job, new ComponentName(jobId.packageName, config.serviceName));
            }
        }
        return jobs;
    }


    public Map.Entry<JobId, JobConfig> findJobByVirtualJobId(int virtualJobId) {
        synchronized (mJobStore) {
            for (Map.Entry<JobId, JobConfig> entry : mJobStore.entrySet()) {
                if (entry.getValue().virtualJobId == virtualJobId) {
                    return entry;
                }
            }
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public JobInfo getPendingJob(int uid, int jobId) {
        JobInfo jobInfo = null;
        synchronized (mJobStore) {
            Iterator<Map.Entry<JobId, JobConfig>> iterator = mJobStore.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<JobId, JobConfig> entry = iterator.next();
                JobId job = entry.getKey();
                if (job.vuid == uid && job.clientJobId == jobId) {
                    jobInfo = mScheduler.getPendingJob(job.clientJobId);
                    break;
                }
            }
        }
        return jobInfo;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public int enqueue(int uid, JobInfo job, VJobWorkItem workItem) {
        if (workItem.get() == null) {
            return -1;
        }
        int id = job.getId();
        ComponentName service = job.getService();
        JobId jobId = new JobId(uid, service.getPackageName(), id);
        JobConfig config;
        synchronized (mJobStore) {
            config = mJobStore.get(jobId);
            if (config == null) {
                int jid = mNextJobId;
                mNextJobId++;
                config = new JobConfig(jid, service.getClassName(), service.getPackageName(), job.getExtras());
                mJobStore.put(jobId, config);
            }
        }
        config.serviceName = service.getClassName();
        config.extras = job.getExtras();

        saveJobs();
        mirror.android.app.job.JobInfo.jobId.set(job, config.virtualJobId);
        mirror.android.app.job.JobInfo.service.set(job, mJobProxyComponent);
        return mScheduler.enqueue(job, workItem.get());
    }


    // We only want to count the jobs that this uid has scheduled on its own
    // behalf, not those that the app has scheduled on someone else's behalf.
    public int countJobsForPkg(String pkgName) {
        int total = 0;
        for (Map.Entry<JobId, JobConfig> entry : mJobStore.entrySet()) {
            if (entry != null && entry.getValue() != null) {
                if (TextUtils.equals(pkgName, entry.getValue().pkgName)) {
                    total++;
                }
            }
        }
        return total;
    }

}
