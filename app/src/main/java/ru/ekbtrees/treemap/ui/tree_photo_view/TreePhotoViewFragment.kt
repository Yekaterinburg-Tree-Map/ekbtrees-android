package ru.ekbtrees.treemap.ui.tree_photo_view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentTreePhotoViewBinding

const val PHOTO_LINK_KEY = "photo_link"

class TreePhotoViewFragment : Fragment() {
    private lateinit var binding: FragmentTreePhotoViewBinding
    private lateinit var photoList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoList = it.getStringArray("PhotoLinkList")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTreePhotoViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PhotoViewAdapter(this, photoList)
        with(binding) {
            viewPager.adapter = adapter
            topAppBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}

class PhotoViewAdapter(fragment: Fragment, private val photoLinkList: Array<String>) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = photoLinkList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = PhotoItemViewFragment()
        fragment.arguments = Bundle().apply {
            val link = photoLinkList[position]
            putString(PHOTO_LINK_KEY, link)
        }
        return fragment
    }
}

class PhotoItemViewFragment : Fragment() {
    private lateinit var photoLink: String
    private lateinit var imageView: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoLink = it.getString(PHOTO_LINK_KEY)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.image_view)
        Glide.with(this).load(photoLink).into(imageView)
    }
}