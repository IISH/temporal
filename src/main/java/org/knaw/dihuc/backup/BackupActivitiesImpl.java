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

import com.google.common.io.Files;
import io.temporal.activity.Activity;
import org.knaw.dihuc.common.Logit;
import org.knaw.dihuc.common.Shell;

import java.io.File;
import java.io.IOException;

/** Store activities implementation. */
public class BackupActivitiesImpl implements BackupActivities {

  private final String hostSpecificTaskQueue;

  private final String failFolder;
  private final String queueFolder;
  private final String workFolder;

  public BackupActivitiesImpl(String hostSpecificTaskQueue, String workFolder, String failFolder, String queueFolder) {
    this.hostSpecificTaskQueue = hostSpecificTaskQueue;
    this.failFolder = failFolder;
    this.queueFolder = queueFolder;
    this.workFolder = workFolder;
  }

  @Override
  public TaskQueueFileNamePair bindTasksToHost(File fileset) {
      return new TaskQueueFileNamePair(hostSpecificTaskQueue, fileset);
  }

  @Override
  public int checksum(File fileset) {
    try {
      return Shell.run(queueFolder + File.separator + "backup" + File.separator + "checksum", fileset);
    } catch (Exception e) {
      Logit.error(e.getMessage());
      throw Activity.wrap(e);
    }
  }

  @Override
  public int rsync(File fileset) {
    try {
      return Shell.run( queueFolder + File.separator + "backup" + File.separator + "rsync", fileset);
    } catch (Exception e) {
      Logit.error(e.getMessage());
      throw Activity.wrap(e);
    }
  }

  @Override
  public void move(File fileset) {
    final File to = new File(workFolder);
    try {
      Files.move(fileset, to);
    } catch (IOException e) {
      Logit.error(e.getMessage());
      throw Activity.wrap(e);
    }
  }
}
