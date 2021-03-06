/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.messageboards.model.impl;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.parsers.bbcode.BBCodeTranslatorUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.messageboards.model.MBCategory;
import com.liferay.portlet.messageboards.model.MBCategoryConstants;
import com.liferay.portlet.messageboards.model.MBDiscussion;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBMessageConstants;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.service.MBCategoryLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBThreadLocalServiceUtil;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class MBMessageImpl extends MBMessageBaseImpl {

	public MBMessageImpl() {
	}

	public String[] getAssetTagNames() throws SystemException {
		return AssetTagLocalServiceUtil.getTagNames(
			MBMessage.class.getName(), getMessageId());
	}

	public List<FileEntry> getAttachmentsFileEntries()
		throws PortalException, SystemException {

		return getAttachmentsFileEntries(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	public List<FileEntry> getAttachmentsFileEntries(int start, int end)
		throws PortalException, SystemException {

		return PortletFileRepositoryUtil.getPortletFileEntries(
			getGroupId(), getAttachmentsFolderId(),
			WorkflowConstants.STATUS_APPROVED, start, end, null);
	}

	public int getAttachmentsFileEntriesCount()
		throws PortalException, SystemException {

		return PortletFileRepositoryUtil.getPortletFileEntriesCount(
			getGroupId(), getAttachmentsFolderId(),
			WorkflowConstants.STATUS_APPROVED);
	}

	public long getAttachmentsFolderId()
		throws PortalException, SystemException {

		if (_attachmentsFolderId > 0) {
			return _attachmentsFolderId;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		long repositoryId = PortletFileRepositoryUtil.getPortletRepository(
			getGroupId(), PortletKeys.MESSAGE_BOARDS, serviceContext);

		Folder folder = PortletFileRepositoryUtil.getPortletFolder(
			getUserId(), repositoryId, getThreadAttachmentsFolderId(),
			String.valueOf(getMessageId()), serviceContext);

		_attachmentsFolderId = folder.getFolderId();

		return _attachmentsFolderId;
	}

	public String getBody(boolean translate) {
		String body = null;

		if (translate) {
			body = BBCodeTranslatorUtil.getHTML(getBody());
		}
		else {
			body = getBody();
		}

		return body;
	}

	public MBCategory getCategory() throws PortalException, SystemException {
		return MBCategoryLocalServiceUtil.getCategory(getCategoryId());
	}

	public List<FileEntry> getDeletedAttachmentsFileEntries()
		throws PortalException, SystemException {

		return getDeletedAttachmentsFileEntries(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	public List<FileEntry> getDeletedAttachmentsFileEntries(int start, int end)
		throws PortalException, SystemException {

		return PortletFileRepositoryUtil.getPortletFileEntries(
			getGroupId(), getAttachmentsFolderId(),
			WorkflowConstants.STATUS_IN_TRASH, start, end, null);
	}

	public int getDeletedAttachmentsFileEntriesCount()
		throws PortalException, SystemException {

		return PortletFileRepositoryUtil.getPortletFileEntriesCount(
			getGroupId(), getAttachmentsFolderId(),
			WorkflowConstants.STATUS_IN_TRASH);
	}

	public MBThread getThread() throws PortalException, SystemException {
		return MBThreadLocalServiceUtil.getThread(getThreadId());
	}

	public long getThreadAttachmentsFolderId()
		throws PortalException, SystemException {

		return getThread().getAttachmentsFolderId();
	}

	public String getWorkflowClassName() {
		if (isDiscussion()) {
			return MBDiscussion.class.getName();
		}
		else {
			return MBMessage.class.getName();
		}
	}

	public boolean isDiscussion() {
		if (getCategoryId() == MBCategoryConstants.DISCUSSION_CATEGORY_ID) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isFormatBBCode() {
		String format = getFormat();

		if (format.equals("bbcode")) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isInTrashThread() throws PortalException, SystemException {
		MBThread thread = getThread();

		if (thread.isInTrash() || thread.isInTrashContainer()) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isReply() {
		return !isRoot();
	}

	public boolean isRoot() {
		if (getParentMessageId() ==
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID) {

			return true;
		}
		else {
			return false;
		}
	}

	public void setAttachmentsFolderId(long attachmentsFolderId) {
		_attachmentsFolderId = attachmentsFolderId;
	}

	private long _attachmentsFolderId;

}