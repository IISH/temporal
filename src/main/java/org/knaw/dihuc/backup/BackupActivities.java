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

import io.temporal.activity.ActivityInterface;

import java.io.File;
import java.net.URL;

@ActivityInterface
public interface BackupActivities {

  final class TaskQueueFileNamePair {
    private String hostTaskQueue;
    private File fileset;

    public TaskQueueFileNamePair(String hostTaskQueue, File fileset) {
      this.hostTaskQueue = hostTaskQueue;
      this.fileset = fileset;
    }

    /** Jackson needs it */
    public TaskQueueFileNamePair() {}

    public String getHostTaskQueue() {
      return hostTaskQueue;
    }

    public File getFileset() {
      return fileset;
    }
  }

  // Calculate the checksum
    int checksum(File fileset);

  // transport the file
    int rsync(File fileset);

  /**
   * Move the file to the work folder
   *
   * @param fileset source file name @@return processed file name
   */
  void move(File fileset);

  /**
   * Pin the fileset to the worker's environment
   *
   * @param fileset remote file location
   * @return local task queue and downloaded file name
   */
  TaskQueueFileNamePair bindTasksToHost(File fileset);
}
