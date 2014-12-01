2s2014-ia896-trending-images
============================

Twitter Trending Images detector from a CBIR perspective

# Introduction
  ------------
  Initially the objective was to detect trending images on Twitter without 
using any meta information (url or image name). During the development of
the project ideas, this concept was changed a bit. Instead of ignoring the
meta information, they are now used to count and trigger when an image should
be analysed.
  Besides that we are now looking for near duplicate images. Perceptual hash
algorithms are being tested to accomplish this task over millions of images.


# Application Requirements
  ------------------------
  1. retrieve twitter tweets sample;

  2. filter only tweets that contains images;

  3. add the image tweet into the database (use url as the record key);

  3.1 if the tweet already have an entry in the database, only increment its
      counter value;

  3.2 if the analysed tweet have more than X (threshold TBD) references,
      process it (extract its perceptual hash and store this information).

  3.3 check if the tweet above is a *duplicate* or a *near duplicate* image,
      when a duplicate/near duplicate image is found, create a reference between
      the images.

  4. every hour, remove references that are outside the application analysis 
     window (24h?);

  5. allow the user to query the system for the last trending images. The
     result must include duplicates and near duplicate images;

# Setup and Access
  ----------------

  Amazon Server Address: (use the credentials provided by prof. Eduardo) 
  https://856197975449.signin.aws.amazon.com/console
  


