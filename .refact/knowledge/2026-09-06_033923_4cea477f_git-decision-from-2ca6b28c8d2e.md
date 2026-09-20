---
id: 3266531a-aac9-4d11-9320-117d9dbd6c68
title: Git decision from 2ca6b28c8d2e
tags:
- decision
- git
created: 2026-09-06
updated: 2026-09-06
filenames:
- tutorials/javaFxTutorialPart1.md
links: []
kind: decision
status: proposed
superseded_by: null
deprecated_at: null
review_after: 2026-09-06
source_chat_id: null
created_at: 2026-09-05T19:39:23.625924+00:00
summary: null
description: null
entities: []
related_files: []
related_entities: []
content_hash: 583fff43015766f2d51a284764f02b39c60cfe6e444d2949003d21c8f775bdaf
source_tool: buddy_memory_lifecycle:git
source_confidence: 0.8199999928474426
source_trajectory_id: null
source_message_range: null
source_commit: 2ca6b28c8d2ebebfe001e1a618a5d5af4094e088
topic: null
last_used_at: null
use_count: 0
last_injected_at: null
dismissed_count: 0
source_content_hash: 583fff43015766f2d51a284764f02b39c60cfe6e444d2949003d21c8f775bdaf
review_needed: true
occurrences: 0
---

Git decision from 2ca6b28c8d2e

Source commit: 2ca6b28c8d2e
Paths: tutorials/javaFxTutorialPart1.md
Summary: JavaFX tutorial: Support cross-platform JARs The OpenJFX plugin expects applications to be modular and bundled with jlink, resulting in fat jars that are not cross-platform. Let's manually include the required dependencies so that shadow can package them properly.