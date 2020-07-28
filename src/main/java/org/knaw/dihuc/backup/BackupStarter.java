/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package org.knaw.dihuc.backup;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.io.File;

/** Starts a file processing sample workflow. */
public class BackupStarter {

  public static void main(String[] args) throws Exception {

    final String _fileset = args[0];
    final File fileset = new File(_fileset);

    // gRPC stubs wrapper that talks to the local docker instance of temporal service.
    WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    // client that can be used to start and signal workflows
    WorkflowClient client = WorkflowClient.newInstance(service);
    BackupWorkflow workflow =
        client.newWorkflowStub(
            BackupWorkflow.class,
            WorkflowOptions.newBuilder().setTaskQueue(BackupWorker.TASK_QUEUE).build());

    Logit.info("Executing BackupWorkflow " + fileset);

    // This is going to block until the workflow completes.
    // This is rarely used in production. Use the commented code below for async start version.
    workflow.processFile(fileset);
    Logit.info("FileProcessingWorkflow completed");

    // Use this code instead of the above blocking call to start workflow asynchronously.
    //    WorkflowExecution workflowExecution =
    //        WorkflowClient.start(workflow::processFile, source, destination);
    //    System.out.println(
    //        "Started periodic workflow with workflowId=\""
    //            + workflowExecution.getWorkflowId()
    //            + "\" and runId=\""
    //            + workflowExecution.getRunId()
    //            + "\"");
    //
    System.exit(0);
  }
}
