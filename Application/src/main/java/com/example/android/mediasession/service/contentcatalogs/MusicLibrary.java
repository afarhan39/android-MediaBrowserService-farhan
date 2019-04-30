/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mediasession.service.contentcatalogs;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.example.android.mediasession.BuildConfig;
import com.example.android.mediasession.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static com.example.android.mediasession.Config.IS_LOCAL_FILE;


public class MusicLibrary {

    private static final String TAG = "YIKES " + MusicLibrary.class.getSimpleName();


    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final HashMap<String, Integer> albumRes = new HashMap<>();
    private static final HashMap<String, String> musicFileName = new HashMap<>();

    static {

        if (IS_LOCAL_FILE) {
            createMediaMetadataCompat(
                    "Jazz_In_Paris",
                    "Jazz in Paris",
                    "Media Right Productions",
                    "Jazz & Blues",
                    "Jazz",
                    103,
                    "jazz_in_paris.mp3",
                    R.drawable.album_jazz_blues,
                    "album_jazz_blues");
            createMediaMetadataCompat(
                    "The_Coldest_Shoulder",
                    "The Coldest Shoulder",
                    "The 126ers",
                    "Youtube Audio Library Rock 2",
                    "Rock",
                    160,
                    "the_coldest_shoulder.mp3",
                    R.drawable.album_youtube_audio_library_rock_2,
                    "album_youtube_audio_library_rock_2");
        } else {
            createMediaMetadataCompatUrl(
                    "{212476D5-DC7A-4826-BB7C-735085718F07}",
                    "Malaysian actuarial science student makes history",
                    "https://cdn.thestar.com.my/Content/Audio/short-with-headline/{212476D5-DC7A-4826-BB7C-735085718F07}_v1.mp3",
                    58
            );
            createMediaMetadataCompatUrl(
                    "{A3C48A99-2CA9-48B4-AC91-688106A85C2E}",
                    "Dr M to TMJ: It’s all in the hands of the people",
                    "https://cdn.thestar.com.my/Content/Audio/short-with-headline/{A3C48A99-2CA9-48B4-AC91-688106A85C2E}_v1.mp3",
                    58
            );
        }
    }

    public static String getRoot() {
        return "root";
    }

    private static String getAlbumArtUri(String albumArtResName) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                BuildConfig.APPLICATION_ID + "/drawable/" + albumArtResName;
    }

    public static String getMusicFilename(String mediaId) {
        Log.d(TAG, "getMusicFilename: ");
        return musicFileName.containsKey(mediaId) ? musicFileName.get(mediaId) : null;
    }

    private static int getAlbumRes(String mediaId) {
        return albumRes.containsKey(mediaId) ? albumRes.get(mediaId) : 0;
    }

    public static Bitmap getAlbumBitmap(Context context, String mediaId) {
        return BitmapFactory.decodeResource(context.getResources(),
                MusicLibrary.getAlbumRes(mediaId));
    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public static MediaMetadataCompat getMetadata(Context context, String mediaId) {
        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
        Bitmap albumArt = getAlbumBitmap(context, mediaId);

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    public static MediaMetadataCompat getMetadataWithUri(Context context, String mediaId) {
        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
        Bitmap albumArt = getAlbumBitmap(context, mediaId);

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

        return builder.build();
    }

    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
            String genre,
            long duration,
            String musicFilename,
            int albumArtResId,
            String albumArtResName) {
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                TimeUnit.MILLISECONDS.convert(duration, TimeUnit.SECONDS))
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                                getAlbumArtUri(albumArtResName))
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                getAlbumArtUri(albumArtResName))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build());
        albumRes.put(mediaId, albumArtResId);
        musicFileName.put(mediaId, musicFilename);
    }

    private static void createMediaMetadataCompatUrl(
            String mediaId,
            String title,
            String audioUrlStream,
            int duration) {
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, audioUrlStream)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "The Star Mobile")
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                TimeUnit.MILLISECONDS.convert(duration, TimeUnit.SECONDS))
                        .build());
        albumRes.put(mediaId, R.drawable.album_thestar);
        musicFileName.put(mediaId, audioUrlStream);
    }
}