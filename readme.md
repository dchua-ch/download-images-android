# Overview
Trying to create a simple android app to download and display 20 images from a given website URL

# To-do
1. Ensure that webview is fully loaded before button to get URLs is available
   - Make button clickable only when webview is fully loaded and ready to be queried (DONE)
2. Add text input for user to specify URL
3. Make button download images
   - Need to ensure that download operation takes place on another thread (i.e. not main/ui thread) (DONE)
   - Delete existing image files to make space for new downloads (DONE) 
   - Need progress bar for download
3. Implement a gridview for images
