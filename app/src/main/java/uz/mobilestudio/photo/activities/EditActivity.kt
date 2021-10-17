package uz.mobilestudio.photo.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import uz.mobilestudio.photo.databinding.ActivityEditBinding
import uz.mobilestudio.photo.edit.*
import uz.mobilestudio.photo.edit.FileSaveHelper.isSdkHigherThan28
import uz.mobilestudio.photo.edit.base.BaseActivity
import uz.mobilestudio.photo.edit.filters.FilterListener
import uz.mobilestudio.photo.edit.filters.FilterViewAdapter
import uz.mobilestudio.photo.edit.tools.EditingToolsAdapter
import uz.mobilestudio.photo.edit.tools.ToolType
import java.io.File
import java.io.IOException
import java.lang.Exception

class EditActivity : BaseActivity(), OnPhotoEditorListener,
    View.OnClickListener,
    PropertiesBSFragment.Properties,
    ShapeBSFragment.Properties,
    EmojiBSFragment.EmojiListener,
    StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {


    private val TAG: String = EditActivity::class.java.simpleName
    val FILE_PROVIDER_AUTHORITY = "uz.mobilestudio.photo.edit.fileprovider"
    private val CAMERA_REQUEST = 52
    private val PICK_REQUEST = 53
    val ACTION_NEXTGEN_EDIT = "action_nextgen_edit"
    val PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE"

    var mPhotoEditor: PhotoEditor? = null
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mShapeBSFragment: ShapeBSFragment? = null
    private var mShapeBuilder: ShapeBuilder? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mTxtCurrentTool: TextView? = null
    private var mWonderFont: Typeface? = null
    private var mRvTools: RecyclerView? = null
    private  var mRvFilters:RecyclerView? = null
    private val mEditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter: FilterViewAdapter = FilterViewAdapter(this)
    private var mRootView: ConstraintLayout? = null
    private val mConstraintSet = ConstraintSet()
    private var mIsFilterVisible = false

    @VisibleForTesting
    var mSaveImageUri: Uri? = null

    private var mSaveFileHelper: FileSaveHelper? = null


    lateinit var binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initViews()

        handleIntentImage(mPhotoEditorView!!.source)

        mWonderFont = Typeface.createFromAsset(assets, "beyond_wonderland.ttf")

        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mStickerBSFragment = StickerBSFragment()
        mShapeBSFragment = ShapeBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        mEmojiBSFragment!!.setEmojiListener(this)
        mPropertiesBSFragment!!.setPropertiesChangeListener(this)
        mShapeBSFragment!!.setPropertiesChangeListener(this)

        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools!!.layoutManager = llmTools
        mRvTools!!.adapter = mEditingToolsAdapter

        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvFilters!!.layoutManager = llmFilters
        mRvFilters!!.adapter = mFilterViewAdapter

        // NOTE(lucianocheng): Used to set integration testing parameters to PhotoEditor

        // NOTE(lucianocheng): Used to set integration testing parameters to PhotoEditor
        val pinchTextScalable =
            intent.getBooleanExtra(PINCH_TEXT_SCALABLE_INTENT_KEY, true)

        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(pinchTextScalable) // set flag to make text scalable when pinch
            //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk


        mPhotoEditor!!.setOnPhotoEditorListener(this)

        //Set Image Dynamically

        //Set Image Dynamically
        mPhotoEditorView!!.source.setImageResource(uz.mobilestudio.photo.R.drawable.paris_tower)

        mSaveFileHelper = FileSaveHelper(this)

    }

    private fun handleIntentImage(source: ImageView) {
        val intent = intent
        if (intent != null) {
            // NOTE(lucianocheng): Using "yoda conditions" here to guard against
            //                     a null Action in the Intent.
            if (Intent.ACTION_EDIT == intent.action || ACTION_NEXTGEN_EDIT == intent.action) {
                try {
                    val uri = intent.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val intentType = intent.type
                if (intentType != null && intentType.startsWith("image/")) {
                    val imageUri = intent.data
                    if (imageUri != null) {
                        source.setImageURI(imageUri)
                    }
                }
            }
        }
    }

    private fun initViews() {
        mPhotoEditorView = findViewById(uz.mobilestudio.photo.R.id.photoEditorView)
        mTxtCurrentTool = findViewById(uz.mobilestudio.photo.R.id.txtCurrentTool)
        mRvTools = findViewById(uz.mobilestudio.photo.R.id.rvConstraintTools)
        mRvFilters = findViewById(uz.mobilestudio.photo.R.id.rvFilterView)
        mRootView = findViewById(uz.mobilestudio.photo.R.id.rootView)
        val imgUndo: ImageView = findViewById(uz.mobilestudio.photo.R.id.imgUndo)
        imgUndo.setOnClickListener(this)
        val imgRedo: ImageView = findViewById(uz.mobilestudio.photo.R.id.imgRedo)
        imgRedo.setOnClickListener(this)
        val imgCamera: ImageView = findViewById(uz.mobilestudio.photo.R.id.imgCamera)
        imgCamera.setOnClickListener(this)
        val imgGallery: ImageView = findViewById(uz.mobilestudio.photo.R.id.imgGallery)
        imgGallery.setOnClickListener(this)
        val imgSave: ImageView = findViewById(uz.mobilestudio.photo.R.id.imgSave)
        imgSave.setOnClickListener(this)
        val imgClose: ImageView = findViewById(uz.mobilestudio.photo.R.id.imgClose)
        imgClose.setOnClickListener(this)
        val imgShare: ImageView = findViewById(uz.mobilestudio.photo.R.id.imgShare)
        imgShare.setOnClickListener(this)
    }

    override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(
            this,
            text!!, colorCode
        )
        textEditorDialogFragment.setOnTextEditorListener { inputText, newColorCode ->
            val styleBuilder = TextStyleBuilder()
            styleBuilder.withTextColor(newColorCode)
            mPhotoEditor!!.editText(rootView!!, inputText, styleBuilder)
            mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_text)
        }
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType) {
        Log.d(
            TAG,
            "onStartViewChangeListener() called with: viewType = [$viewType]"
        )
    }

    override fun onStopViewChangeListener(viewType: ViewType) {
        Log.d(
            TAG,
            "onStopViewChangeListener() called with: viewType = [$viewType]"
        )
    }

    override fun onTouchSourceImage(event: MotionEvent) {
        Log.d(TAG, "onTouchView() called with: event = [$event]")
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            uz.mobilestudio.photo.R.id.imgUndo -> mPhotoEditor!!.undo()
            uz.mobilestudio.photo.R.id.imgRedo -> mPhotoEditor!!.redo()
            uz.mobilestudio.photo.R.id.imgSave -> saveImage()
            uz.mobilestudio.photo.R.id.imgClose -> onBackPressed()
            uz.mobilestudio.photo.R.id.imgShare -> shareImage()
            uz.mobilestudio.photo.R.id.imgCamera -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            uz.mobilestudio.photo.R.id.imgGallery -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    PICK_REQUEST
                )
            }
        }
    }

    private fun shareImage() {
        if (mSaveImageUri == null) {
            showSnackbar(getString(uz.mobilestudio.photo.R.string.msg_save_image_to_share))
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(mSaveImageUri!!))
        startActivity(Intent.createChooser(intent, getString(uz.mobilestudio.photo.R.string.msg_share_image)))
    }

    private fun buildFileProviderUri(uri: Uri): Uri? {
        return FileProvider.getUriForFile(
            this,
            FILE_PROVIDER_AUTHORITY,
            File(uri.path)
        )
    }


    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission || isSdkHigherThan28()) {
            showLoading("Saving...")
            mSaveFileHelper!!.createFile(fileName) { fileCreated, filePath, error, uri ->
                if (fileCreated) {
                    val saveSettings = SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build()
                    mPhotoEditor!!.saveAsFile(filePath, saveSettings, object : OnSaveListener {
                        override fun onSuccess(imagePath: String) {
                            mSaveFileHelper!!.notifyThatFileIsNowPubliclyAvailable(contentResolver)
                            hideLoading()
                            showSnackbar("Image Saved Successfully")
                            mSaveImageUri = uri
                            mPhotoEditorView!!.source.setImageURI(mSaveImageUri)
                        }

                        override fun onFailure(exception: Exception) {
                            hideLoading()
                            showSnackbar("Failed to save Image")
                        }
                    })
                } else {
                    hideLoading()
                    showSnackbar(error)
                }
            }
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    mPhotoEditor!!.clearAllViews()
                    val photo = data?.extras!!["data"] as Bitmap?
                    mPhotoEditorView!!.source.setImageBitmap(photo)
                }
                PICK_REQUEST -> try {
                    mPhotoEditor!!.clearAllViews()
                    val uri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    mPhotoEditorView!!.source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeColor(colorCode))
        mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_brush)
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeOpacity(opacity))
        mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_brush)
    }

    override fun onShapeSizeChanged(shapeSize: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeSize(shapeSize.toFloat()))
        mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_brush)
    }

    override fun onShapePicked(shapeType: ShapeType?) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeType(shapeType))
    }

    override fun onEmojiClick(emojiUnicode: String?) {
        mPhotoEditor!!.addEmoji(emojiUnicode)
        mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_emoji)
    }

    override fun onStickerClick(bitmap: Bitmap?) {
        mPhotoEditor!!.addImage(bitmap)
        mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_sticker)
    }

    override fun isPermissionGranted(isGranted: Boolean, permission: String?) {
        if (isGranted) {
            saveImage()
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(uz.mobilestudio.photo.R.string.msg_save_image))
        builder.setPositiveButton(
            "Save"
        ) { dialog: DialogInterface?, which: Int -> saveImage() }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setNeutralButton(
            "Discard"
        ) { dialog: DialogInterface?, which: Int -> finish() }
        builder.create().show()
    }

    override fun onFilterSelected(photoFilter: PhotoFilter?) {
        mPhotoEditor!!.setFilterEffect(photoFilter)
    }

    override fun onToolSelected(toolType: ToolType?) {
        when (toolType) {
            ToolType.SHAPE -> {
                mPhotoEditor!!.setBrushDrawingMode(true)
                mShapeBuilder = ShapeBuilder()
                mPhotoEditor!!.setShape(mShapeBuilder)
                mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_shape)
                showBottomSheetDialogFragment(mShapeBSFragment)
            }
            ToolType.TEXT -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)
                    mPhotoEditor!!.addText(inputText, styleBuilder)
                    mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_text)
                }
            }
            ToolType.ERASER -> {
                mPhotoEditor!!.brushEraser()
                mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_eraser_mode)
            }
            ToolType.FILTER -> {
                mTxtCurrentTool!!.setText(uz.mobilestudio.photo.R.string.label_filter)
                showFilter(true)
            }
            ToolType.EMOJI -> showBottomSheetDialogFragment(mEmojiBSFragment)
            ToolType.STICKER -> showBottomSheetDialogFragment(mStickerBSFragment)
        }
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }


    private fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)
        if (isVisible) {
            mConstraintSet.clear(mRvFilters!!.id, ConstraintSet.START)
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(mRvFilters!!.id, ConstraintSet.END)
        }
        val changeBounds = ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        TransitionManager.beginDelayedTransition(mRootView!!, changeBounds)
        mConstraintSet.applyTo(mRootView)
    }

    override fun onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false)
            mTxtCurrentTool!!.setText(R.string.app_name)
        } else if (!mPhotoEditor!!.isCacheEmpty) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }
}