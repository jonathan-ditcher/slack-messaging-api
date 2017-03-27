package com.ditcherj.slack;

import com.ditcherj.slack.dto.Scope;
import com.ditcherj.slack.responses.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jonathanditcher
 * Date: 23/03/17
 * Time: 13:36
 * To change this template use File | Settings | File Templates.
 */
public class SlackMessagingApi {

    private static final Logger logger = LoggerFactory.getLogger(SlackMessagingApi.class);
    private static final String END_POINT = "https://slack.com/api/";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String TIMESTAMP_FORMAT = "#.000000";
    private static final Set<Scope> DEFAULT_SCOPES = new HashSet<>(Arrays.asList(
            Scope.CHANNELS_READ,
            Scope.CHAT_WRITE_USER,
            Scope.CHAT_WRITE_BOT,
            Scope.IM_READ,
            Scope.USERS_READ
    ));

    private String clientId;
    private String clientSecret;

    public SlackMessagingApi(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public AccessTokenResponse getAccessToken(String code, String redirect_uri) {
        logger.trace("code[{}] redirect_uri[{}]", code, redirect_uri);

        final String endpoint = "oauth.access";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("client_id", this.clientId));
        params.add(new BasicNameValuePair("client_secret", this.clientSecret));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirect_uri));

        AccessTokenResponse accessTokenResponse = this.executePost(params, endpoint, AccessTokenResponse.class);

        return accessTokenResponse;
    }

    public RevokeTokenResponse revokeAccessToken(String token) {
        logger.trace("token[{}]", token);

        final String endpoint = "oauth.revoke ";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", token));

        RevokeTokenResponse revokeTokenResponse = this.executePost(params, endpoint, RevokeTokenResponse.class);

        return revokeTokenResponse;
    }

    public BotInfoResponse getBotInfo(String accessToken, String bot) {
        logger.trace("accessToken[{}] bot[{bot}]", accessToken, bot);

        final String endpoint = "bots.info";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));

        if(!StringUtils.isEmpty(bot))
            params.add(new BasicNameValuePair("bot", bot));

        BotInfoResponse botInfoResponse = this.executeGet(params, endpoint, BotInfoResponse.class);

        return botInfoResponse;
    }

    // channel methods

    public ChannelHistoryResponse getChannelHistory(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "channels.history";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        ChannelHistoryResponse channelHistoryResponse = this.executeGet(params, endpoint, ChannelHistoryResponse.class);

        return channelHistoryResponse;
    }

    public ChannelRepliesResponse getChannelReplies(String accessToken, String channel, Double thread_ts) {
        logger.trace("accessToken[{}] channel[{}] thread_ts[{}]", accessToken, channel, thread_ts);

        final String endpoint = "channels.replies";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        DecimalFormat df = new DecimalFormat(TIMESTAMP_FORMAT);
        params.add(new BasicNameValuePair("thread_ts", df.format(thread_ts)));

        ChannelRepliesResponse channelRepliesResponse = this.executeGet(params, endpoint, ChannelRepliesResponse.class);

        return channelRepliesResponse;
    }

    public ChannelInfoResponse getChannelInfo(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "channels.info";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        ChannelInfoResponse channelInfoResponse = this.executeGet(params, endpoint, ChannelInfoResponse.class);

        return channelInfoResponse;
    }

    public ChannelListResponse getChannelList(String accessToken) {
        logger.trace("accessToken[{}]", accessToken);

        final String endpoint = "channels.list";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));

        ChannelListResponse channelListResponse = this.executeGet(params, endpoint, ChannelListResponse.class);

        return channelListResponse;
    }

    public SlackResponse archiveChannel(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "channels.archive";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        SlackResponse response = this.executeGet(params, endpoint, SlackResponse.class);

        return response;
    }

    public ChannelResponse createChannel(String accessToken, String name) {
        logger.trace("accessToken[{}] name[{}]", accessToken, name);

        final String endpoint = "channels.create";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("name", name));

        ChannelResponse response = this.executePost(params, endpoint, ChannelResponse.class);

        return response;
    }

    public ChannelResponse inviteUserToChannel(String accessToken, String channel, String user) {
        logger.trace("accessToken[{}] channel[{}] user[{}]", accessToken, channel, user);

        final String endpoint = "channels.create";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("user", user));

        ChannelResponse response = this.executePost(params, endpoint, ChannelResponse.class);

        return response;
    }

    public ChannelResponse joinChannel(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "channels.join";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("name", channel));

        ChannelResponse response = this.executePost(params, endpoint, ChannelResponse.class);

        return response;
    }

    public SlackResponse kickUserFromChannel(String accessToken, String channel, String user) {
        logger.trace("accessToken[{}] channel[{}] user[{}]", accessToken, channel, user);

        final String endpoint = "channels.kick";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("user", user));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse leaveChannel(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "channels.leave";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("name", channel));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse markChannel(String accessToken, String channel, Double timestamp) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "channels.mark";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("name", channel));

        DecimalFormat df = new DecimalFormat(TIMESTAMP_FORMAT);
        params.add(new BasicNameValuePair("ts", df.format(timestamp)));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public ChannelResponse renameChannel(String accessToken, String channel, String name) {
        logger.trace("accessToken[{}] channel[{}] name[{}]", accessToken, channel, name);

        final String endpoint = "channels.rename";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("name", name));

        ChannelResponse response = this.executePost(params, endpoint, ChannelResponse.class);

        return response;
    }

    public SlackResponse setChannelPurpose(String accessToken, String channel, String purpose) {
        logger.trace("accessToken[{}] channel[{}] purpose[{}]", accessToken, channel, purpose);

        final String endpoint = "channels.setPurpose";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("purpose", purpose));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse setChannelTopic(String accessToken, String channel, String topic) {
        logger.trace("accessToken[{}] channel[{}] topic[{}]", accessToken, channel, topic);

        final String endpoint = "channels.setTopic";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("topic", topic));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse unarchiveChannel(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "channels.unarchive";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    // chat methods

    public SlackResponse deleteChatMessage(String accessToken, String channel, Double timestamp) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "chat.delete";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        DecimalFormat df = new DecimalFormat(TIMESTAMP_FORMAT);
        params.add(new BasicNameValuePair("ts", df.format(timestamp)));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse sendMeMessage(String accessToken, String channel, String text) {
        logger.trace("accessToken[{}] channel[{}] text[{}]", accessToken, channel, text);

        final String endpoint = "chat.meMessage";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("as_user", "true"));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse postMessage(String accessToken, String channel, String text) {
        logger.trace("accessToken[{}] channel[{}] text[{}]", accessToken, channel, text);

        final String endpoint = "chat.postMessage";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("text", text));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse updateMessage(String accessToken, String channel, String text, Double timestamp) {
        logger.trace("accessToken[{}] channel[{}] text[{}]", accessToken, channel, text);

        final String endpoint = "chat.update";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("text", text));

        DecimalFormat df = new DecimalFormat(TIMESTAMP_FORMAT);
        params.add(new BasicNameValuePair("ts", df.format(timestamp)));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    // dnd methods

    public SlackResponse endDnd(String accessToken) {
        logger.trace("accessToken[{}]", accessToken);

        final String endpoint = "dnd.endDnd";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse endSnooze(String accessToken) {
        logger.trace("accessToken[{}]", accessToken);

        final String endpoint = "dnd.endSnooze";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public DndInfoResponse getDndInfo(String accessToken, String user) {
        logger.trace("accessToken[{}] user[{}]", accessToken, user);

        final String endpoint = "dnd.info";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("user", user));

        DndInfoResponse response = this.executeGet(params, endpoint, DndInfoResponse.class);

        return response;
    }

    public DndInfoResponse setSnooze(String accessToken, Integer num_minutes) {
        logger.trace("accessToken[{}] num_minutes[{}]", accessToken, num_minutes);

        final String endpoint = "dnd.setSnooze";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("num_minutes", String.valueOf(num_minutes)));

        DndInfoResponse response = this.executePost(params, endpoint, DndInfoResponse.class);

        return response;
    }

    // emoji methods

    public EmojiListResponse getEmojiList(String accessToken) {
        logger.trace("accessToken[{}]", accessToken);

        final String endpoint = "emoji.liste";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));

        EmojiListResponse response = this.executeGet(params, endpoint, EmojiListResponse.class);

        return response;
    }

    // files comments methods

    public FileCommentResponse addCommentToFile(String accessToken, String file, String comment) {
        logger.trace("accessToken[{}] file[{}] comment[{}]", accessToken, file, comment);

        final String endpoint = "files.comments.add";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("file", file));
        params.add(new BasicNameValuePair("comment", comment));

        FileCommentResponse response = this.executePost(params, endpoint, FileCommentResponse.class);

        return response;
    }

    public SlackResponse deleteCommentFromFile(String accessToken, String file, String commentId) {
        logger.trace("accessToken[{}] file[{}] commentId[{}]", accessToken, file, commentId);

        final String endpoint = "files.comments.delete";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("file", file));
        params.add(new BasicNameValuePair("id", commentId));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public FileCommentResponse editCommentOnFile(String accessToken, String file, String commentId, String comment) {
        logger.trace("accessToken[{}] file[{}] commentId[{}] comment[{}]", accessToken, file, commentId, comment);

        final String endpoint = "files.comments.edit";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("file", file));
        params.add(new BasicNameValuePair("id", commentId));
        params.add(new BasicNameValuePair("comment", comment));

        FileCommentResponse response = this.executePost(params, endpoint, FileCommentResponse.class);

        return response;
    }

    // files methods

    public SlackResponse deleteFile(String accessToken, String file) {
        logger.trace("accessToken[{}] file[{}]", accessToken, file);

        final String endpoint = "files.delete";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("file", file));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public FileResponse getFileInfo(String accessToken, String file) {
        logger.trace("accessToken[{}] file[{}]", accessToken, file);

        final String endpoint = "files.info";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("file", file));

        FileResponse response = this.executeGet(params, endpoint, FileResponse.class);

        return response;
    }

    public ListFilesResponse listFiles(String accessToken) {
        logger.trace("accessToken[{}]", accessToken);

        final String endpoint = "files.list";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));

        ListFilesResponse response = this.executeGet(params, endpoint, ListFilesResponse.class);

        return response;
    }

    public FileResponse enablePublicFileSharing(String accessToken, String file) {
        logger.trace("accessToken[{}] file[{}]", accessToken, file);

        final String endpoint = "files.sharedPublicURL";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("file", file));

        FileResponse response = this.executeGet(params, endpoint, FileResponse.class);

        return response;
    }

    public FileResponse revokeublicFileSharing(String accessToken, String file) {
        logger.trace("accessToken[{}] file[{}]", accessToken, file);

        final String endpoint = "files.revokePublicURL";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("file", file));

        FileResponse response = this.executeGet(params, endpoint, FileResponse.class);

        return response;
    }
/*
https://api.slack.com/methods/files.upload
    public FileResponse uploadFile(String accessToken, File file) {
        logger.trace("accessToken[{}] file[{}]", accessToken, file);

        final String endpoint = "files.upload";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("file", file));

        FileResponse response = this.executePost(params, endpoint, FileResponse.class);

        return response;
    }
*/

    // group methods

    public SlackResponse archivePrivateChannel(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "groups.archive";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse closePrivateChannel(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "groups.close";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        SlackResponse response = this.executePost(params, endpoint, SlackResponse.class);

        return response;
    }

    public GroupResponse createPrivateChannel(String accessToken, String name) {
        logger.trace("accessToken[{}] name[{}]", accessToken, name);

        final String endpoint = "groups.create";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("name", name));

        GroupResponse response = this.executePost(params, endpoint, GroupResponse.class);

        return response;
    }

    public GroupResponse createChildPrivateChannel(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "groups.createChild";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        GroupResponse response = this.executePost(params, endpoint, GroupResponse.class);

        return response;
    }

    public GroupsHistoryResponse getGroupsHistory(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "groups.history";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        GroupsHistoryResponse response = this.executeGet(params, endpoint, GroupsHistoryResponse.class);

        return response;
    }

    public GroupResponse getGroupInfo(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "groups.info";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        GroupResponse response = this.executeGet(params, endpoint, GroupResponse.class);

        return response;
    }

    public GroupResponse inviteUserToPrivateChannel(String accessToken, String channel, String user) {
        logger.trace("accessToken[{}] channel[{}] user[{}]", accessToken, channel, user);

        final String endpoint = "groups.invite";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("user", user));

        GroupResponse response = this.executeGet(params, endpoint, GroupResponse.class);

        return response;
    }

    public SlackResponse removeUserFromPrivateChannel(String accessToken, String channel, String user) {
        logger.trace("accessToken[{}] channel[{}] user[{}]", accessToken, channel, user);

        final String endpoint = "groups.kick";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("user", user));

        SlackResponse response = this.executeGet(params, endpoint, SlackResponse.class);

        return response;
    }

    public SlackResponse leavePrivateChannel(String accessToken, String channel) {
        logger.trace("accessToken[{}] channel[{}]", accessToken, channel);

        final String endpoint = "groups.leave";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));

        SlackResponse response = this.executeGet(params, endpoint, SlackResponse.class);

        return response;
    }

    public GroupsListResponse listPrivateChannels(String accessToken) {
        logger.trace("accessToken[{}]", accessToken);

        final String endpoint = "groups.list";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));

        GroupsListResponse response = this.executeGet(params, endpoint, GroupsListResponse.class);

        return response;
    }

    public PostMessageResponse sendDirectMessage(String accessToken, String channel, String text) {
        logger.trace("accessToken[{}] channel[{}] text[{}]", accessToken, channel, text);

        final String endpoint = "chat.postMessage";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("token", accessToken));
        params.add(new BasicNameValuePair("channel", channel));
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("as_user", "true"));

        PostMessageResponse messageResponse = this.executePost(params, endpoint, PostMessageResponse.class);

        return messageResponse;
    }

    public ListChannelsResponse listIMChannels(String accessToken) {
        logger.trace("accessToken["+accessToken+"]");

        final String endpoint = "im.list";
        ListChannelsResponse listChannelsResponse = this.executeGet(Collections.singletonList(new BasicNameValuePair("token", accessToken)), endpoint, ListChannelsResponse.class);

        return listChannelsResponse;
    }

    public ListUsersResponse listUsers(String accessToken) {
        logger.trace("accessToken["+accessToken+"]");

        final String endpoint = "users.list";
        ListUsersResponse listUsersResponse = this.executeGet(Collections.singletonList(new BasicNameValuePair("token", accessToken)), endpoint, ListUsersResponse.class);

        return listUsersResponse;
    }

    private <T extends SlackResponse> T executeGet(List<NameValuePair> params, String endpoint, Class<T> clazz) {
        logger.trace("params["+params+"]");

        T slackResponse = null;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpGet request = new HttpGet(END_POINT + endpoint);

            URI uri = new URIBuilder(request.getURI()).addParameters(params).build();
            request.setURI(uri);

            logger.trace("executing request " + request.getRequestLine());
            HttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();

            ObjectMapper objectMapper = new ObjectMapper();
            slackResponse = objectMapper.readValue(entity.getContent(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return slackResponse;
    }

    private <T extends SlackResponse> T executePost(List<NameValuePair> params, String endpoint, Class<T> clazz) {
        logger.trace("endpoint[{}] clazz[{}] params[{}]", endpoint, clazz, params);

        T slackResponse = null;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpPost httppost = new HttpPost(END_POINT + endpoint);

            httppost.setEntity(new UrlEncodedFormEntity(params));

            logger.trace("executing request " + httppost.getRequestLine());
            HttpResponse response = httpClient.execute(httppost);

            HttpEntity entity = response.getEntity();

            ObjectMapper objectMapper = new ObjectMapper();
            slackResponse = objectMapper.readValue(entity.getContent(), clazz);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return slackResponse;
    }

    public String getAuthorizationURI(String redirectURI) {
        return this.getAuthorizationURI(redirectURI, new ArrayList<>(DEFAULT_SCOPES));
    }

    public String getAuthorizationURI(String redirectURI, Scope... scopes) {
        List<Scope> scopesList = new LinkedList<>();
        for(Scope scope : scopes) {
            scopesList.add(scope);
        }

        return this.getAuthorizationURI(redirectURI, scopesList);
    }

    private String getAuthorizationURI(String redirectURI, List<Scope> scopes) {
        String uri = null;
        try {
            uri = "https://slack.com/oauth/authorize?client_id=" + this.clientId +
                    "&scope=" + URLEncoder.encode(StringUtils.join(scopes, " "), DEFAULT_ENCODING) +
                    "&redirect_uri=" + URLEncoder.encode(redirectURI, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return uri;
    }
}