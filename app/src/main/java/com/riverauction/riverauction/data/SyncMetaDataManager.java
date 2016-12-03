package com.riverauction.riverauction.data;

import com.riverauction.riverauction.api.model.CSubjectGroup;

import java.util.List;

import rx.Observable;

// TODO: 나중에 meta data 받아올게 늘으면 만들기
public class SyncMetaDataManager {
    private DataManager dataManager;

    public SyncMetaDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

//    public Observable<SyncMetadata> getAll() {
//        return Observable.combineLatest(
//                dataManager.getSubjectGroups(),
//                );
//    }

    /**
     * 앱 킬때 동기화해야되는 데이터
     */
    public static class SyncMetadata {
        private List<CSubjectGroup> subjectGroups;

        public SyncMetadata(List<CSubjectGroup> subjectGroups) {
            this.subjectGroups = subjectGroups;
        }

        public boolean hasAllNecessaryContents() {
            return null != subjectGroups && !subjectGroups.isEmpty();
        }
    }
}
