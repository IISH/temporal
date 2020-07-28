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
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.lang.management.ManagementFactory;

/**
 * This is the process that hosts all workflows and activities in this sample. Run multiple
 * instances of the worker in different windows. Then start a workflow by running the
 * FileProcessingStarter. Note that all activities always execute on the same worker. But each time
 * they might end up on a different worker as the first activity is dispatched to the common task
 * list.
 */
public class BackupWorker {

  static final String TASK_QUEUE = "FileProcessing";

  static final String WORK = "Work";
  static final String FAILED = "Failed";

  public static void main(String[] args) {

    // arg[0] = work folder
    // arg[1] = fail folder

    final String workFolder = args[0];
    final String failFolder = args[1];
    final String queueFolder = args[2];

    String hostSpecifiTaskQueue = ManagementFactory.getRuntimeMXBean().getName();

    // gRPC stubs wrapper that talks to the local docker instance of temporal service.
    WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    // client that can be used to start and signal workflows
    WorkflowClient client = WorkflowClient.newInstance(service);

    // worker factory that can be used to create workers for specific task queues
    WorkerFactory factory = WorkerFactory.newInstance(client);
    // Worker that listens on a task queue and hosts both workflow and activity implementations.
    final Worker workerForCommonTaskQueue = factory.newWorker(TASK_QUEUE);
    workerForCommonTaskQueue.registerWorkflowImplementationTypes(BackupWorkflowImpl.class);
    BackupActivitiesImpl storeActivityImpl = new BackupActivitiesImpl(hostSpecifiTaskQueue, workFolder, failFolder, queueFolder);
    workerForCommonTaskQueue.registerActivitiesImplementations(storeActivityImpl);

    // Get worker to poll the host-specific task queue.
    final Worker workerForHostSpecificTaskQueue = factory.newWorker(hostSpecifiTaskQueue);
    workerForHostSpecificTaskQueue.registerActivitiesImplementations(storeActivityImpl);

    // Start all workers created by this factory.
    factory.start();
    Logit.info("Worker started for task queue: " + TASK_QUEUE);
    Logit.info("Worker Started for activity task Queue: " + hostSpecifiTaskQueue);
  }
}
