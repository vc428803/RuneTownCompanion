package com.runetown;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.runetown.lifeprogression.application.ProcessEvidenceCandidateService;
import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.evidence.EvidenceCandidate;
import com.runetown.lifeprogression.domain.evidence.RuleBasedQualificationPolicy;

public class App {

    public static void main(String[] args) {

        CollectionEntry collection = new CollectionEntry(
                "collection-001",
                "RuneTown Companion",
                "建立 Nexelyth Life Progression 第一階段原型",
                LocalDate.now()
        );

        EvidenceCandidate candidate = new EvidenceCandidate(
                "完成 Life Progression 第一階段",
                "建立 Collection、Evidence 與 Qualification 流程",
                "GitHub Repository",
                LocalDateTime.now()
        );

        RuleBasedQualificationPolicy policy =
                new RuleBasedQualificationPolicy();

        ProcessEvidenceCandidateService service =
                new ProcessEvidenceCandidateService(policy);

        boolean accepted = service.process(candidate, collection);

        System.out.println("Evidence accepted: " + accepted);
        System.out.println("Collection: " + collection.getTitle());
        System.out.println("Evidence count: " + collection.getEvidences().size());
    }
}