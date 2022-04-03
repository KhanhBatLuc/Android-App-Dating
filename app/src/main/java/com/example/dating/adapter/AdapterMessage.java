package com.example.dating.adapter;

import android.app.Activity;

import com.example.dating.R;
import com.example.dating.activity.ChatBoxActivity;
import com.example.dating.application.AppConfig;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Message;
import com.squareup.picasso.Picasso;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter {
    private List<Message> MessageList;
    private Activity activity;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SERVER = 3;


    public AdapterMessage(List<Message> MessagesList, Activity activity) {
        this.MessageList = MessagesList;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }


    @Override
    public int getItemViewType(int position) {
        Message message = MessageList.get(position);

        if (message.getEventType() == 2) {
            return VIEW_TYPE_MESSAGE_SERVER;
        }
        if (message.getFrom().getId().equals(new SessionManager(activity).getUniqueIdentifier())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_row_message_send, parent, false);
                return new SentMessageHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_row_message_received, parent, false);
                return new ReceivedMessageHolder(view);
            case VIEW_TYPE_MESSAGE_SERVER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_row_chat_server_message, parent, false);
                return new ServerMessageHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Message message = MessageList.get(position);

        Log.i("onBindViewHolder message", message.toString());
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message, position);
                break;
            case VIEW_TYPE_MESSAGE_SERVER:
                ((ServerMessageHolder) holder).bind(message);
                break;
        }

    }

    private class ServerMessageHolder extends RecyclerView.ViewHolder {
        TextView txtContent;

        ServerMessageHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
        }

        void bind(Message message) {
            txtContent.setText(message.getContent());

        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView txtContent, txtTime;
        ImageView sentImage;
        private Boolean click = false;

        SentMessageHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.txtMessageContent);
            txtTime = (TextView) itemView.findViewById(R.id.txtMessageTime);
            sentImage = (ImageView) itemView.findViewById(R.id.sentImage);
        }

        void bind(Message message) {
            Log.e("SentMessageHolder", "SentMessageHolder");
            txtTime.setText(message.getCreatedAtTime());
            if (message.getContentType() == ChatBoxActivity.PICTURE) {
                txtContent.setVisibility(View.GONE);
                sentImage.setVisibility(View.VISIBLE);
                Picasso.get().load(AppConfig.IMAGE_URL + "messages/" + message.getContent()).into(sentImage);
                sentImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!click)
                            txtTime.setVisibility(View.VISIBLE);
                        else txtTime.setVisibility(View.GONE);
                        click = !click;
                    }
                });
            } else if (message.getContentType() == ChatBoxActivity.TEXT) {
                sentImage.setVisibility(View.GONE);
                txtContent.setVisibility(View.VISIBLE);
                txtContent.setText(message.getContent());
                txtContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!click)
                            txtTime.setVisibility(View.VISIBLE);
                        else txtTime.setVisibility(View.GONE);
                        click = !click;
                    }
                });
            } else if (message.getContentType() == ChatBoxActivity.VIDEO) {

                Log.i("SentMessageHolder VIDEO  ", message.toString());
                sentImage.setVisibility(View.GONE);
                txtContent.setVisibility(View.VISIBLE);

                if (message.getReadStatus() == ChatBoxActivity.DENY) {
                    Log.i("SentMessageHolder VIDEO DENY ", "SentMessageHolder VIDEO DENY ");
                    if (message.getFrom().getId().equalsIgnoreCase(new SessionManager(activity).getUniqueIdentifier()))
                        txtContent.setText("Bạn đã từ chối cuộc gọi");
                    else
                        txtContent.setText(message.getFrom().getName() + " đã từ chối cuộc gọi");
                } else if (message.getReadStatus() == ChatBoxActivity.STOP) {
                    Log.i("SentMessageHolder VIDEO STOP ", "SentMessageHolder VIDEO STOP ");
                    txtContent.setText("Cuộc gọi đã kết thúc sau " + message.getDuration());
                } else
                    txtContent.setVisibility(View.GONE);

//                txtContent.setTextSize(18);
//                txtContent.setTextColor(R.color.black);
//                txtContent.setTypeface(Typeface.DEFAULT_BOLD);

                txtContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!click)
                            txtTime.setVisibility(View.VISIBLE);
                        else txtTime.setVisibility(View.GONE);
                        click = !click;
                    }
                });
            }

        }

    }


    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView txtContent, txtTime, txtUserName;
        ImageView imageView, receiveImage;
        private Boolean click = false;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtUserName = (TextView) itemView.findViewById(R.id.txtUserName);
            imageView = (ImageView) itemView.findViewById(R.id.imgProfile);
            receiveImage = (ImageView) itemView.findViewById(R.id.receiveImage);
        }

        void bind(Message message, int position) {

            Log.e("ReceivedMessageHolder", "ReceivedMessageHolder");
            if (position > 0) {
                Message previousMessage = MessageList.get(position - 1);
                if (previousMessage.getFrom() != null) {
                    if (previousMessage.getFrom().getId().equals(message.getFrom().getId())) {
                        txtUserName.setVisibility(View.GONE);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }
            }

            txtTime.setText(message.getCreatedAtTime());
            Picasso.get().load(AppConfig.IMAGE_URL + message.getFrom().getAvatar()).into(imageView);
            txtUserName.setText(message.getFrom().getName());

            if (message.getContentType() == ChatBoxActivity.PICTURE) {
                txtContent.setVisibility(View.GONE);
                receiveImage.setVisibility(View.VISIBLE);
                Picasso.get().load(AppConfig.IMAGE_URL + "messages/" + message.getContent()).into(receiveImage);
                receiveImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!click)
                            txtTime.setVisibility(View.VISIBLE);
                        else txtTime.setVisibility(View.GONE);
                        click = !click;
                    }
                });
            } else if (message.getContentType() == ChatBoxActivity.TEXT) {
                receiveImage.setVisibility(View.GONE);
                txtContent.setVisibility(View.VISIBLE);

                txtContent.setText(message.getContent());
                txtContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!click)
                            txtTime.setVisibility(View.VISIBLE);
                        else txtTime.setVisibility(View.GONE);
                        click = !click;
                    }
                });
            } else if (message.getContentType() == ChatBoxActivity.VIDEO) {

                Log.i("ReceivedMessageHolder VIDEO  ", message.toString());
                receiveImage.setVisibility(View.GONE);
                txtContent.setVisibility(View.VISIBLE);
                if (message.getReadStatus() == ChatBoxActivity.DENY) {
                    Log.i("ReceivedMessageHolder VIDEO DENY ", "ReceivedMessageHolder VIDEO DENY ");
                    if (message.getFrom().getId().equalsIgnoreCase(new SessionManager(activity).getUniqueIdentifier()))
                        txtContent.setText("Bạn đã từ chối cuộc gọi");
                    else
                        txtContent.setText(message.getFrom().getName() + " đã từ chối cuộc gọi");
                } else if (message.getReadStatus() == ChatBoxActivity.STOP) {
                    Log.i("ReceivedMessageHolder VIDEO STOP ", "ReceivedMessageHolder VIDEO STOP ");
                    txtContent.setText("Cuộc gọi đã kết thúc sau " + message.getDuration());
                } else
                    txtContent.setVisibility(View.GONE);

//                txtContent.setTextSize(18);
//                txtContent.setTextColor(R.color.black);
//                txtContent.setTypeface(Typeface.DEFAULT_BOLD);

                txtContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!click)
                            txtTime.setVisibility(View.VISIBLE);
                        else txtTime.setVisibility(View.GONE);
                        click = !click;
                    }
                });
            }

        }

    }


}