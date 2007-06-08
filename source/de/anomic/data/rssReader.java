//rssReader.java
//------------
// part of YACY
//
// (C) 2007 Alexander Schier
//
// last change: $LastChangedDate:  $ by $LastChangedBy: $
// $LastChangedRevision: $
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package de.anomic.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;

public class rssReader {
	URL url;
	ChannelIF channel;
	TreeSet feedItems;
	public rssReader(String url) throws MalformedURLException{
		this.url=new URL(url);
		ChannelBuilder builder=new ChannelBuilder();
		try {
			channel=FeedParser.parse(builder, url);
			Collection oldfeedItems=channel.getItems();
			feedItems=new TreeSet(new rssReaderItemComparator());
			Iterator it=oldfeedItems.iterator();
			int count=0;
			while(it.hasNext()){
				de.nava.informa.impl.basic.Item item=(de.nava.informa.impl.basic.Item) it.next();
				rssReaderItem newItem=new rssReaderItem(count++, item.getLink(), item.getTitle(), item.getDescription(), item.getDate(), item.getCreator());
				feedItems.add(newItem);
			}
		}
		catch (IOException e) {} 
		catch (ParseException e) {}
	}
	public String getCreator(){
		return (channel!=null)? channel.getCreator(): null;
	}
	public String getTitle(){
		return (channel!=null)? channel.getTitle(): null;
	}
	public String getDescription(){
		return (channel!=null)? channel.getDescription(): null;
	}
	public Collection getFeedItems(){
		return feedItems;
	}
	
}
