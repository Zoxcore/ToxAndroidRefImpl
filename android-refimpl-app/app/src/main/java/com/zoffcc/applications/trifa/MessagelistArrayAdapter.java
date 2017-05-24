/**
 * [TRIfA], Java part of Tox Reference Implementation for Android
 * Copyright (C) 2017 Zoff <zoff@zoff.cc>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package com.zoffcc.applications.trifa;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import static com.zoffcc.applications.trifa.MainActivity.get_filetransfer_filenum_from_id;
import static com.zoffcc.applications.trifa.MainActivity.set_filetransfer_accepted_from_id;
import static com.zoffcc.applications.trifa.MainActivity.set_filetransfer_state_from_id;
import static com.zoffcc.applications.trifa.MainActivity.set_message_accepted_from_id;
import static com.zoffcc.applications.trifa.MainActivity.set_message_state_from_id;
import static com.zoffcc.applications.trifa.MainActivity.tox_file_control;
import static com.zoffcc.applications.trifa.MainActivity.tox_friend_by_public_key__wrapper;
import static com.zoffcc.applications.trifa.TRIFAGlobals.TRIFA_MSG_TYPE.TRIFA_MSG_FILE;
import static com.zoffcc.applications.trifa.ToxVars.TOX_FILE_CONTROL.TOX_FILE_CONTROL_CANCEL;
import static com.zoffcc.applications.trifa.ToxVars.TOX_FILE_CONTROL.TOX_FILE_CONTROL_PAUSE;
import static com.zoffcc.applications.trifa.ToxVars.TOX_FILE_CONTROL.TOX_FILE_CONTROL_RESUME;

public class MessagelistArrayAdapter extends ArrayAdapter<Message>
{
    private static final String TAG = "trifa.MessagelstAAdptr";
    private final Context context;
    private final List<Message> values;

    public MessagelistArrayAdapter(Context context, List<Message> values)
    {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    synchronized public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;

        try
        {
            if (values.get(position).TRIFA_MESSAGE_TYPE == TRIFA_MSG_FILE.value)
            {
                // FILE -------------
                if (values.get(position).direction == 0)
                {
                    // incoming file
                    if (values.get(position).state == TOX_FILE_CONTROL_CANCEL.value)
                    {
                        // ------- STATE: CANCEL -------------
                        rowView = inflater.inflate(R.layout.message_list_ft_incoming, parent, false);

                        ImageButton button_ok = (ImageButton) rowView.findViewById(R.id.ft_button_ok);
                        button_ok.setVisibility(View.GONE);

                        ImageButton button_cancel = (ImageButton) rowView.findViewById(R.id.ft_button_cancel);
                        button_cancel.setVisibility(View.GONE);

                        ProgressBar ft_progressbar = (ProgressBar) rowView.findViewById(R.id.ft_progressbar);
                        ft_progressbar.setVisibility(View.GONE);

                        TextView textView = (TextView) rowView.findViewById(R.id.m_text);
                        textView.setText("" + values.get(position).text + "\n*canceled*");
                        // ------- STATE: CANCEL -------------
                    }
                    else if (values.get(position).state == TOX_FILE_CONTROL_PAUSE.value)
                    {
                        // ------- STATE: PAUSE -------------
                        if (values.get(position).ft_accepted == false)
                        {
                            // not yet accepted
                            rowView = inflater.inflate(R.layout.message_list_ft_incoming, parent, false);

                            final ImageButton button_ok = (ImageButton) rowView.findViewById(R.id.ft_button_ok);
                            final Drawable d1 = new IconicsDrawable(parent.getContext()).icon(GoogleMaterial.Icon.gmd_check_circle).backgroundColor(Color.TRANSPARENT).color(Color.parseColor("#EF088A29")).sizeDp(50);
                            button_ok.setImageDrawable(d1);

                            final ImageButton button_cancel = (ImageButton) rowView.findViewById(R.id.ft_button_cancel);
                            final Drawable d2 = new IconicsDrawable(parent.getContext()).icon(GoogleMaterial.Icon.gmd_highlight_off).backgroundColor(Color.TRANSPARENT).color(Color.parseColor("#A0FF0000")).sizeDp(50);
                            button_cancel.setImageDrawable(d2);

                            final ProgressBar ft_progressbar = (ProgressBar) rowView.findViewById(R.id.ft_progressbar);

                            button_ok.setOnTouchListener(new View.OnTouchListener()
                            {
                                @Override
                                public boolean onTouch(View v, MotionEvent event)
                                {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                                    {
                                        try
                                        {
                                            // accept FT
                                            tox_file_control(tox_friend_by_public_key__wrapper(values.get(position).tox_friendpubkey), get_filetransfer_filenum_from_id(values.get(position).filetransfer_id), TOX_FILE_CONTROL_RESUME.value);
                                            values.get(position).ft_accepted = true;
                                            values.get(position).state = TOX_FILE_CONTROL_RESUME.value;
                                            set_filetransfer_accepted_from_id(values.get(position).filetransfer_id);
                                            set_filetransfer_state_from_id(values.get(position).filetransfer_id, TOX_FILE_CONTROL_RESUME.value);
                                            set_message_accepted_from_id(values.get(position).id);
                                            set_message_state_from_id(values.get(position).id, TOX_FILE_CONTROL_RESUME.value);
                                            button_ok.setVisibility(View.GONE);
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                    }
                                    else
                                    {
                                    }
                                    return true;
                                }
                            });


                            button_cancel.setOnTouchListener(new View.OnTouchListener()
                            {
                                @Override
                                public boolean onTouch(View v, MotionEvent event)
                                {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                                    {
                                        try
                                        {
                                            // cancel FT
                                            Log.i(TAG, "button_cancel:OnTouch:001");
                                            values.get(position).state = TOX_FILE_CONTROL_CANCEL.value;
                                            Log.i(TAG, "button_cancel:OnTouch:002");
                                            tox_file_control(tox_friend_by_public_key__wrapper(values.get(position).tox_friendpubkey), get_filetransfer_filenum_from_id(values.get(position).filetransfer_id), TOX_FILE_CONTROL_CANCEL.value);
                                            Log.i(TAG, "button_cancel:OnTouch:003");
                                            set_filetransfer_state_from_id(values.get(position).filetransfer_id, TOX_FILE_CONTROL_CANCEL.value);
                                            Log.i(TAG, "button_cancel:OnTouch:004");
                                            set_message_state_from_id(values.get(position).id, TOX_FILE_CONTROL_CANCEL.value);
                                            Log.i(TAG, "button_cancel:OnTouch:005");

                                            button_ok.setVisibility(View.GONE);
                                            Log.i(TAG, "button_cancel:OnTouch:006");
                                            button_cancel.setVisibility(View.GONE);
                                            Log.i(TAG, "button_cancel:OnTouch:007");
                                            ft_progressbar.setVisibility(View.GONE);
                                            Log.i(TAG, "button_cancel:OnTouch:008");
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                    }
                                    else
                                    {
                                    }
                                    return true;
                                }
                            });

                            TextView textView = (TextView) rowView.findViewById(R.id.m_text);

                            // TODO: maybe make the FT text here?
                            textView.setText("" + values.get(position).text);

                            ft_progressbar.setIndeterminate(true);
                            ft_progressbar.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            // has accepted
                        }
                        // ------- STATE: PAUSE -------------
                    }
                    else
                    {
                        // ------- STATE: RESUME -------------

                        // ------- STATE: RESUME -------------
                    }
                }
                else
                {
                    // outgoing file
                }
                // FILE -------------
            }
            else
            {
                // TEXT -------------
                if (values.get(position).direction == 0)
                {
                    // msg to me
                    if (values.get(position).read)
                    {
                        rowView = inflater.inflate(R.layout.message_list_entry_read, parent, false);
                    }
                    else
                    {
                        rowView = inflater.inflate(R.layout.message_list_entry, parent, false);
                    }
                }
                else
                {
                    // msg from me
                    if (values.get(position).read)
                    {
                        rowView = inflater.inflate(R.layout.message_list_self_entry_read, parent, false);
                    }
                    else
                    {
                        rowView = inflater.inflate(R.layout.message_list_self_entry, parent, false);
                    }
                }

                TextView textView = (TextView) rowView.findViewById(R.id.m_text);
                textView.setText("#" + values.get(position).id + ":" + values.get(position).text);

                ImageView imageView = (ImageView) rowView.findViewById(R.id.m_icon);

                if (!values.get(position).read)
                {
                    // not yet read
                    imageView.setImageResource(R.drawable.circle_red);
                }
                else
                {
                    // msg read by other party
                    imageView.setImageResource(R.drawable.circle_green);
                }
                // TEXT -------------
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return rowView;
    }
}
